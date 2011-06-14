package JIRCd.Events;

import JIRCd.Client;

public class ClientDisconnectEvent extends Event {
	private Client Client;
	public ClientDisconnectEvent(String EventType, Client Client){
		super(EventType);
		this.Client = Client;
	}
	
	public Client GetClient(){
		return Client;
	}
}
