package JIRCd.Events;

import JIRCd.Client;
import JIRCd.Channel;

public class ClientJoinChannelEvent extends Event {
	private Client Client;
	private Channel Channel;
	public ClientJoinChannelEvent(String EventType, Client Client, Channel Channel){
		super(EventType);
		this.Client = Client;
		this.Channel = Channel;
	}
	
	public Client GetClient() {
		return Client;
	}
	
	public Channel GetChannel() {
		return Channel;
	}
}
