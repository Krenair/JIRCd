package JIRCd.Events;

public class Event {
	private String EventType;
	public Event(String EventType){
		this.EventType = EventType;
	}
	
	public String GetType(){
		return EventType;
	}
}
