package JIRCd;

import JIRCd.Plugins.Plugin;
import JIRCd.Plugins.PluginEventManager;
import JIRCd.Plugins.PluginInfo;
import JIRCd.Plugins.PluginLoader;

public class ConsoleLineHandler extends Thread {
	public boolean KeepGoing = true;
	public void run(){
		String Line = "";
		while(KeepGoing) {
			Line = System.console().readLine().trim();
			Main.ConsoleCommandsLog.info("In: " + Line, false);
			String[] Split = Line.split(" ");
			if (Split[0].equalsIgnoreCase("shutdown")) { 
				System.exit(1);
			} else if (Split[0].equalsIgnoreCase("listplugins")) {
				for (Plugin Plugin : Main.Plugins){
					PluginInfo Info = Plugin.GetPluginInformation();
					Main.ConsoleCommandsLog.info(Info.GetName() + " by " + Info.GetAuthor() + ", description: " + Info.GetDescription());
				}
			} else if (Split[0].equalsIgnoreCase("disableplugin")) {
				if (Split.length < 2) {
					Main.ConsoleCommandsLog.info("You need to specify the name of a plugin to disable.");
					continue;
				}
				
				for (Plugin Plugin : Main.Plugins) {
					if (Plugin.GetPluginInformation().GetName().equalsIgnoreCase(Split[1])) {
						PluginEventManager.UnloadPlugin(Plugin);
					}
				}
			} else if (Split[0].equalsIgnoreCase("enableplugin")) {
				if (Split.length < 2) {
					Main.ConsoleCommandsLog.info("You need to specify the name of a plugin to load.");
					continue;
				}
				
				if (!PluginLoader.LoadIndividualPlugin(Split[1])) {
					Main.ConsoleCommandsLog.info("An error occured while loading " + Split[1] + ". This might be due to a missing file.");
				}
			} else if (Split[0].equalsIgnoreCase("help")) {
				
			}
		}
	}
}
