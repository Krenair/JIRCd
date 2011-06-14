package JIRCd;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import JIRCd.Events.ClientDisconnectEvent;
import JIRCd.GUI.MainScreen;
import JIRCd.Plugins.PluginEventManager;

public class Client{
	public int ThreadID;
	public Socket Socket;
	public String RemoteIP = "";
	public BufferedReader InFromClient;
	public DataOutputStream OutToClient;
	public boolean HasAuthed = false;
	public int Auth = 0;
	public String Username = "";
	public String Nickname = "";
	public String Hostname = "secret";
	public String Realname = "";
	public List<String> ChannelNames = new ArrayList<String>();
	
	public boolean IsOperator = false;
	public boolean IsBot = false;
	public boolean IsService = false;
	public boolean IsAuthed = false;
	
	public Client(Socket Socket, int ID){
		this.Socket = Socket;
		ThreadID = ID;
		RemoteIP = Socket.getInetAddress().toString().substring(1);
		Main.ConnectionLog.info("New client on IP " + RemoteIP + "! ThreadID: " + ThreadID);
		List<String> ClientNames = new ArrayList<String>();
		for (Client Client : Main.Clients.toArray(new Client[]{})) {
			ClientNames.add(Client.Nickname);
		}
		MainScreen.ClientList.setListData(ClientNames.toArray(new String[]{}));
		try {
			InFromClient = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
			OutToClient = new DataOutputStream(Socket.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String GetMode(){
		StringBuffer output = new StringBuffer("+");
		if(this.IsOperator)
			output.append("o");
		
		if(this.IsBot)
			output.append("B");
		
		if(this.IsService)
			output.append("s");
		
		if(this.IsAuthed)
			output.append("r");
		
		return output.toString();
	}
	
	public void SendLine(String Text) {
		try {
			this.OutToClient.writeBytes(Text + "\r\n");
			Main.RawIOLog.info("To " + ThreadID + ": \"" + Text + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void CleanDisconnect(String Reason) {
		try {
			Socket.close();
		} catch (IOException e) {}
		List<Client> SendQuitTo = new ArrayList<Client>();
		for(String ChannelName : this.ChannelNames){
			for(Client Client : Main.Channels.get(ChannelName).Members){
				if(Client != this && !SendQuitTo.contains(Client)){
					SendQuitTo.add(Client);
				}
			}
			Main.Channels.get(ChannelName).RemoveMember(this);
		}
		String Message = ":" + this.Nickname + "!" + this.Username + "@" + this.Hostname + " QUIT";
		if(Reason != null)
			Message += " :" + Reason;

		for (Client Client : SendQuitTo){
			Client.SendLine(Message);
		}
		
		if(Main.Clients.contains(this))
			Main.Clients.remove(this);
		
		Main.ConnectionLog.info("Client on IP " + RemoteIP + " disconnected! ThreadID: " + ThreadID);
		PluginEventManager.FireEvent(new ClientDisconnectEvent("OnClientDisconnect", this));
		List<String> ClientNames = new ArrayList<String>();
		for (Client Client : Main.Clients.toArray(new Client[]{})) {
			ClientNames.add(Client.Nickname);
		}
		MainScreen.ClientList.setListData(ClientNames.toArray(new String[]{}));
	}
	
	public void finalize() {
		CleanDisconnect(null);
	}
}