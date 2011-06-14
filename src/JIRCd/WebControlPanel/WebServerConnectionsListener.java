package JIRCd.WebControlPanel;

import java.io.IOException;
import java.net.ServerSocket;

import JIRCd.Main;

public class WebServerConnectionsListener extends Thread {
	public static boolean KeepGoing = true;
	public void run(){
		ServerSocket ServerSocket = null;
		try {
			ServerSocket = new ServerSocket(6665);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int i = 0;
		Main.MainLog.info("[WebControl] Listening on port " + ServerSocket.getLocalPort() + " for incoming connections.");
		while(KeepGoing){
			try {
				Main.WebClients.add(new WebServerClient(ServerSocket.accept(), i));
				i++;
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}