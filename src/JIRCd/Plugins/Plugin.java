package JIRCd.Plugins;

public interface Plugin {
	public PluginInfo GetPluginInformation();
	
	public void OnEnable();
	
	public void OnDisable();
}
