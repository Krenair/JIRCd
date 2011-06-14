package JIRCd.Events;

import JIRCd.Channel;
import JIRCd.Client;

public class ClientChangeChannelTopicEvent extends Event {
	private Client Client;
	private Channel Channel;
	private String Topic;
	public ClientChangeChannelTopicEvent(String EventType, Client Client, Channel Channel, String Topic){
		super(EventType);
		this.Client = Client;
		this.Channel = Channel;
		this.Topic = Topic;
	}
	
	public Client GetClient(){
		return Client;
	}
	
	public Channel GetChannel(){
		return Channel;
	}
	
	public String GetTopic(){
		return Topic;
	}
}
