package JIRCd.Events;

import JIRCd.Client;
import JIRCd.Channel;

public class ClientPartChannelEvent extends Event {
	private Client Client;
	private Channel Channel;
	public ClientPartChannelEvent(String EventType, Client Client, Channel Channel){
		super(EventType);
		this.Client = Client;
		this.Channel = Channel;
	}
	
	public Client GetClient() {
		return Client;
	}
	
	public Channel GetChannelName() {
		return Channel;
	}
}
