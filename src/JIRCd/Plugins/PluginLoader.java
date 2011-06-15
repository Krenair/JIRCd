package JIRCd.Plugins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

import JIRCd.Main;

public class PluginLoader {
	public static void LoadAll(){
		File PluginsDirectory = new File("plugins");
		String[] PluginsDirectoryChildren = PluginsDirectory.list();
		if (PluginsDirectoryChildren == new String[]{null}) {
			Main.MainLog.info("No 'plugins' directory exists (or is not a directory). No plugins loaded.");
		} else {
		    for (int I = 0; I < PluginsDirectoryChildren.length; I++){
		    	if (PluginsDirectoryChildren[I].length() > 4 && PluginsDirectoryChildren[I].substring(PluginsDirectoryChildren[I].length() - 4, PluginsDirectoryChildren[I].length()).equals(".jar") && new File("plugins/" + PluginsDirectoryChildren[I]).exists()){
		    		LoadIndividualPlugin(PluginsDirectoryChildren[I]);
		    	}
		    }
		}
	}
	
	public static boolean LoadIndividualPlugin(String Filename) {
    	try {
        	File PluginFile = new File("plugins/" + Filename);
        	if (!PluginFile.exists()) {
        		Main.MainLog.warning("Plugin loader was called, but " + Filename + " was not found.");
        		return false;
        	}
        	File PluginInfoFile = new File("plugins/" + Filename.substring(0, Filename.length() - 4) + ".plugininfo");
        	if (!PluginFile.exists()) {
        		Main.MainLog.warning("Plugin loader was called, but " + "plugins/" + Filename.substring(0, Filename.length() - 4) + ".plugininfo" + " was not found.");
        		return false;
        	}
    		DataInputStream in = new DataInputStream(new FileInputStream(PluginInfoFile));
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String FullyQualifiedName = br.readLine();
    		in.close();
    		URLClassLoader ClassLoader = new URLClassLoader(new URL[]{PluginFile.toURI().toURL()}, Main.class.getClassLoader());
    		Class<?> PluginClass = Class.forName(FullyQualifiedName, true, ClassLoader);
    		Plugin PluginInstance = (Plugin) PluginClass.newInstance();
			PluginInstance.OnEnable();
			Main.Plugins.add(PluginInstance);
			PluginInfo PluginInformation = PluginInstance.GetPluginInformation();
			Main.MainLog.info("Added plugin '" + PluginInformation.GetName() + "' by " + PluginInformation.GetAuthor() + ".");
			return true;
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}