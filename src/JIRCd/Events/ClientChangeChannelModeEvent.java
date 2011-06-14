package JIRCd.Events;

import JIRCd.Client;
import JIRCd.Channel;

public class ClientChangeChannelModeEvent extends Event {
	private Client Client;
	private Channel Channel;
	private String AddModes;
	private String RemoveModes;
	public ClientChangeChannelModeEvent(String EventType, Client Client, Channel Channel, String AddModes, String RemoveModes){
		super(EventType);
		this.Client = Client;
		this.Channel = Channel;
		this.AddModes = AddModes;
		this.RemoveModes = RemoveModes;
	}
	
	public Client GetClient(){
		return Client;
	}
	
	public Channel GetChannel(){
		return Channel;
	}
	
	public String GetAddModes(){
		return AddModes;
	}
	
	public String GetRemoveModes(){
		return RemoveModes;
	}
}
