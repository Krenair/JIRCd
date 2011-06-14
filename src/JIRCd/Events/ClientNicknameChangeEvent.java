package JIRCd.Events;

import JIRCd.Client;

public class ClientNicknameChangeEvent extends Event {
	private Client Client;
	public ClientNicknameChangeEvent(String EventType, Client Client){
		super(EventType);
		this.Client = Client;
	}
	
	public Client GetClient() {
		return Client;
	}
}
