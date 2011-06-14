package JIRCd.Plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import JIRCd.Main;
import JIRCd.Events.Event;
import JIRCd.Events.EventListener;

public class PluginEventManager {
	private static List<TypeListenerPluginGroup> EventListenerPluginGroups = new ArrayList<TypeListenerPluginGroup>();
	
	public static boolean RegisterEvent(String EventType, EventListener EventListener, Plugin Plugin) {
		EventListenerPluginGroups.add(new TypeListenerPluginGroup(EventType, EventListener, Plugin));
		return true;
	}
	
	public static boolean UnregisterEvent(String EventType, EventListener EventListener, Plugin Plugin) {
		boolean Removed = false;
		TypeListenerPluginGroup[] ELPGList = EventListenerPluginGroups.toArray(new TypeListenerPluginGroup[]{});
		for (TypeListenerPluginGroup Group : ELPGList) {
			if (Group.GetEventType().equals(EventType) && Group.GetEventListener().equals(EventListener) && Group.GetPlugin().equals(Plugin)) {
				EventListenerPluginGroups.remove(Group);
				Removed = true;
			}
		}
		return Removed;
	}
	
	public static void FireEvent(Event Event) {
		TypeListenerPluginGroup[] ELPGList = EventListenerPluginGroups.toArray(new TypeListenerPluginGroup[]{});
		for(TypeListenerPluginGroup ELPG : ELPGList) {
			if(ELPG.GetEventType().equals(Event.GetType())){
				EventListener EventListener = ELPG.GetEventListener();
				for(Method ListenerMethod : EventListener.getClass().getMethods()){
					if(ListenerMethod.getName().equals(Event.GetType())){
						try {
							ListenerMethod.invoke(EventListener, new Object[]{Event}); //Instance of the event listener, parameters 
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static void UnloadPlugin(Plugin Plugin) {
		Plugin.OnDisable();
		for (TypeListenerPluginGroup TLPGroup : EventListenerPluginGroups) {
			if (TLPGroup.GetPlugin().equals(Plugin)) {
				EventListenerPluginGroups.remove(TLPGroup);
			}
		}
		
		for (Plugin PluginListEntry : Main.Plugins) {
			if (Plugin.equals(PluginListEntry)) {
				Main.Plugins.remove(Plugin);
			}
		}
	}
}
