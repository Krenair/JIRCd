package JIRCd.WebControlPanel;

import java.io.IOException;

import JIRCd.Main;

public class WebServerMessagesListener extends Thread{
	public static boolean KeepGoing = true;
	public void run() {
		while(KeepGoing){
			WebServerClient[] ClientList = Main.WebClients.toArray(new WebServerClient[]{});
			for (WebServerClient WebClient : ClientList) {
				try {
					if(WebClient.Socket.isConnected() && WebClient.InFromClient.ready()) {
						WebClientLineHandler LineHandler = new WebClientLineHandler(WebClient);
						LineHandler.start();
					} else if (!WebClient.Socket.isConnected()) {
						Main.WebServerConnectionsLog.info("Connection from " + WebClient.RemoteIP + " died.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
