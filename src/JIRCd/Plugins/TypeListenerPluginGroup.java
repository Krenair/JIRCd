package JIRCd.Plugins;

import JIRCd.Events.EventListener;

public class TypeListenerPluginGroup {
	private String EventType;
	private EventListener EventListener;
	private Plugin Plugin;
	
	public TypeListenerPluginGroup(String EventType, EventListener EventListener, Plugin Plugin) {
		this.EventType = EventType;
		this.EventListener = EventListener;
		this.Plugin = Plugin;
	}
	
	public String GetEventType() {
		return EventType;
	}
	
	public EventListener GetEventListener() {
		return EventListener;
	}
	
	public Plugin GetPlugin() {
		return Plugin;
	}
}
