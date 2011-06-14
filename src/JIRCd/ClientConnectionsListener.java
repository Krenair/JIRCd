package JIRCd;
import java.io.IOException;
import java.net.ServerSocket;

public class ClientConnectionsListener extends Thread {
	public static boolean KeepGoing = true;
	public void run(){
		ServerSocket ServerSocket = null;
		try {
			ServerSocket = new ServerSocket(Main.clientConnectionsPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.ServerSocketAddress = ServerSocket.getLocalSocketAddress();
		int i = 0;
		Main.ConnectionLog.info("Listening on port " + ServerSocket.getLocalPort() + " for incoming connections.");
		while(KeepGoing){
			try {
				Main.Clients.add(new Client(ServerSocket.accept(), i));
				i++;
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}