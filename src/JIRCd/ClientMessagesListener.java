package JIRCd;

import java.io.IOException;

public class ClientMessagesListener extends Thread{
	public static boolean KeepGoing = true;
	public void run() {
		while(KeepGoing){
			Client[] ClientList = Main.Clients.toArray(new Client[]{});
			for (Client Client : ClientList) {
				try {
					//if(Client.Socket.isConnected() && Client.InFromClient.ready()) {
					if(Client.InFromClient.ready()) {
						ClientLineHandler LineHandler = new ClientLineHandler(Client);
						LineHandler.start();
					} else if (!Client.Socket.isConnected()) {
						Main.ConnectionLog.info("Connection from " + Client.RemoteIP + " died.");
						Client.CleanDisconnect("Read error.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
