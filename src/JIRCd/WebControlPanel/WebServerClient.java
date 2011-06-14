package JIRCd.WebControlPanel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import JIRCd.Main;

public class WebServerClient {
	public boolean KeepGoing = true;
	public Socket Socket;
	public String RemoteIP = "";
	public BufferedReader InFromClient;
	public DataOutputStream OutToClient;
	public int ThreadID;
	public String RequestedPage;
	public WebServerClient(Socket Socket, int ID) {
		this.Socket = Socket;
		ThreadID = ID;
		RemoteIP = Socket.getInetAddress().toString().substring(1);
		Main.WebServerConnectionsLog.info("New client on IP " + RemoteIP + "! ThreadID: " + ThreadID);
		try {
			InFromClient = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
			OutToClient = new DataOutputStream(Socket.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void SendLine(String Text){
		try {
			this.OutToClient.writeBytes(Text + "\r\n");
			Main.WebServerIOLog.info("To " + ThreadID + ": \"" + Text + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void CloseSocket(){
		try {
			Socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finalize(){
		CloseSocket();
	}
}
