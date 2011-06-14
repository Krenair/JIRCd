package JIRCd.Events;

import JIRCd.Client;

public class ClientAuthEvent extends Event {
	private Client Client;
	public ClientAuthEvent(String EventType, Client Client){
		super(EventType);
		this.Client = Client;
	}
	
	public Client GetClient() {
		return Client;
	}
}
