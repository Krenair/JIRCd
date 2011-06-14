package JIRCd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import JIRCd.GUI.MainScreen;
import JIRCd.Plugins.Plugin;
import JIRCd.Plugins.PluginLoader;
import JIRCd.WebControlPanel.WebServerClient;
/*import JIRCd.WebControlPanel.WebServerConnectionsListener;
import JIRCd.WebControlPanel.WebServerMessagesListener;*/

public class Main{
	public static String Version = "v0.1";
	
	public static String ServerName = "localhost";
	public static String NetworkName = "localnet";
	public static SocketAddress ServerSocketAddress = null;
	public static String TimeStarted = "";
	public static String[] MOTDLines = new String[]{""};
    public static List<Client> Clients = new ArrayList<Client>();
    public static List<WebServerClient> WebClients = new ArrayList<WebServerClient>();
    public static List<Plugin> Plugins = new ArrayList<Plugin>();
    public static ConcurrentHashMap<String,Channel> Channels = new ConcurrentHashMap<String,Channel>();
    public static int ChannelNum = 0;
    public static PropertiesFile Properties = new PropertiesFile("jircd.properties");
	public static int clientConnectionsPort = Properties.getInt("client-listen-port");
	public static int ServerLeafConnectionsPort = Properties.getInt("server-leaf-listen-port");
	public static Logger MainLog = new Logger("JIRCd-main", "logs/Main.log");
	public static Logger RawIOLog = new Logger("JIRCd-raw", "logs/SendReceive.log", false);
	public static Logger ConnectionLog = new Logger("JIRCd-connections", "logs/Connections.log");
	public static Logger ConsoleCommandsLog = new Logger("JIRCd-console", "logs/ConsoleCommands.log");
	public static Logger WebServerConnectionsLog = new Logger("JIRCd-webcontrol-connections", "logs/WebServerConnections.log");
	public static Logger WebServerIOLog = new Logger("JIRCd-webcontrol-raw", "logs/WebServerSendReceive.log", false);
	public static Logger WebServerRequestsLog = new Logger("JIRCd-webcontrol-requests", "logs/WebServerRequests.log");
	
	public static ConsoleLineHandler ConsoleLineHandlerThread = null;
	public static ClientConnectionsListener ClientConnectionsListenerThread = null;
	public static ClientMessagesListener ClientMessagesListenerThread = null;
	/*public static WebServerConnectionsListener WebServerConnectionsListenerThread = null;
	public static WebServerMessagesListener WebServerMessagesListenerThread = null;
	public static ServerConnectionsListener ServerConnectionsListener = null;
	public static RemoteServerHubConnection RemoteServerHub = null;*/
    public static Timer Timer = new Timer();
	
    public static void main(String[] Args){
    	ConsoleLineHandlerThread = new ConsoleLineHandler();
    	ConsoleLineHandlerThread.start();
    	JIRCd.GUI.MainScreen.main();
    	System.out.println("JIRCd " + Version);
    	PluginLoader.LoadAll();
		/*if (properties.getBoolean("hub")){
			serverConnectionsListener = new ServerConnectionsListener();
			serverConnectionsListener.run();
		}
		
		if (properties.getBoolean("leaf")){
			remoteServerHub = new RemoteServerHubConnection();
			remoteServerHub.run();
		}*/
		
		try {
		    BufferedReader in = new BufferedReader(new FileReader("MOTD"));
		    String str = "";
		    int i = 0;
		    while ((str = in.readLine()) != null) {
		        MOTDLines[i] = str;
		        i++;
		    }
		    in.close();
		} catch (Exception e) {
			System.out.println("Error reading MOTD file.");
			e.printStackTrace();
		}
		
		/*WebServerConnectionsListenerThread = new WebServerConnectionsListener();
		WebServerConnectionsListenerThread.start();
		WebServerMessagesListenerThread = new WebServerMessagesListener();
		WebServerMessagesListenerThread.start();*/
		
		TimeStarted = new SimpleDateFormat("EEE MMM d yyyy 'at' HH:mm:ss z").format(Calendar.getInstance().getTime());
		ClientConnectionsListenerThread = new ClientConnectionsListener();
		ClientConnectionsListenerThread.start();
		ClientMessagesListenerThread = new ClientMessagesListener();
		ClientMessagesListenerThread.start();
		
		Timer.schedule(new TimerTask() {
			public void run() {
				if (Main.Clients.size() == 0) {
					return;
				}
				List<String> ClientNames = new ArrayList<String>();
				for (Client Client : Main.Clients) {
					ClientNames.add(Client.Nickname);
				}
				MainScreen.ClientList.setListData(ClientNames.toArray(new String[]{}));
			}
		}, 0, 2000);
		
		/*Timer.schedule(new TimerTask(){
			public void run() {
				System.out.println("Sending ping.");
				Client[] ClientList = Main.Clients.toArray(new Client[]{});
				for (Client Client : ClientList){
					if (!Client.Socket.isClosed() && Client.HasAuthed) {
						final String Randomness = Double.toString(Math.random());
						Client.SendText("PING :" + Randomness);
						class PingResponseChecker extends Thread{
							private Client Client;
							private long Timesent;
							public PingResponseChecker (Client Client, long Timesent){
								this.Client = Client;
								this.Timesent = Timesent;
							}
							public void run(){
								try {
									while(!Client.Socket.isClosed() && !Client.InFromClient.ready() && System.currentTimeMillis() > Timesent + 120000){}
									if (Client.InFromClient.ready()){
										String In = Client.InFromClient.readLine();
										if (!In.trim().equals("PONG :" + Randomness)){
											run();
										}
									} else {
										Client.SendText("ERROR :No pong? Okay. Disconnecting you.");
										Client.CloseSocket();
										Main.Clients.remove(Client);
										Client.finalize();
									}
								} catch (SocketException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						new PingResponseChecker(Client, System.currentTimeMillis()).start();
					}
				}
			}
		}, 120000);*/
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ClientMessagesListener.KeepGoing = false;
				ClientConnectionsListener.KeepGoing = false;
				String Message = ServerName + " shutting down.";
				Client[] ClientList = Main.Clients.toArray(new Client[]{});
				for (Client Client : ClientList){
					Client.SendLine(Message);
					Client.CleanDisconnect(Message);
				}
				
				Plugin[] PluginList = Main.Plugins.toArray(new Plugin[]{});
				for (Plugin Plugin : PluginList) {
					Plugin.OnDisable();
				}
				ConsoleLineHandlerThread.KeepGoing = false;
			}
		});
	}
    
    public static boolean EqualsOneOf(String String, String[] Valid){
    	for (String Check : Valid){
    		if (Check.equals(String)){
    			return true;
    		}
    	}
    	return false;
    }
    
    /*
     * TODO: Finish GUI
     * TODO: User/channel relation modes (ban, exception, owner, protected op, operator, halfop, voice)
     * TODO: Finish web server.
     * TODO: Add support for other servers in a network
     */
}