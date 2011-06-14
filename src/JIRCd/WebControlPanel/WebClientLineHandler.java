package JIRCd.WebControlPanel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import JIRCd.Main;

public class WebClientLineHandler extends Thread{
	private WebServerClient Client;
	private String Line;
	public WebClientLineHandler(WebServerClient Client){
		try {
			this.Client = Client;
			this.Line = Client.InFromClient.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		Main.WebServerIOLog.info("From " + Client.ThreadID + ": \"" + Line + "\"");
		String[] Split = Line.split(" ");
		if(Split[0].equalsIgnoreCase("GET")) {
			Client.RequestedPage = Split[1];
		} else if (Split[0].equalsIgnoreCase("")) {
			//Client has finished request, do stuff.
			try {
				StringBuffer Buffer = new StringBuffer("");
			    BufferedReader in = new BufferedReader(new FileReader("web" + Client.RequestedPage));
			    String PageLine = "";
			    while ((PageLine = in.readLine()) != null) {
			        Buffer.append(PageLine);
			    }
			    in.close();
			    Client.SendLine(Buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Client.CloseSocket();
			return;
		}
	}
}
