package JIRCd.Events;

import JIRCd.Channel;
import JIRCd.Client;

public class ClientSendChannelMessageEvent extends Event {
	private Client From;
	private Channel To;
	private String Message;
	public ClientSendChannelMessageEvent(String EventType, Client From, Channel To, String Message){
		super(EventType);
		this.From = From;
		this.To = To;
		this.Message = Message;
	}
	
	public Client GetFrom(){
		return From;
	}
	
	public Channel GetTo(){
		return To;
	}
	
	public String GetMessage(){
		return Message;
	}
}
