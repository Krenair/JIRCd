package JIRCd.Events;

import JIRCd.Client;

public class ClientSendPrivateMessageEvent extends Event {
	private Client From;
	private Client To;
	private String Message;
	public ClientSendPrivateMessageEvent (String EventType, Client From, Client To, String Message){
		super(EventType);
		this.From = From;
		this.To = To;
		this.Message = Message;
	}
	
	public Client GetFrom(){
		return From;
	}
	
	public Client GetTo(){
		return To;
	}
	
	public String GetMessage(){
		return Message;
	}
}
