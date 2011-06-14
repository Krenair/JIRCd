package JIRCd.Plugins;

public class PluginInfo {
	private String Name = "";
	private String Author = "";
	private String Description = "";
	
	public PluginInfo(String Name, String Author, String Description) {
		this.Name = Name;
		this.Author = Author;
		this.Description = Description;
	}
	
	public String GetName() {
		return Name;
	}
	
	public String GetAuthor() {
		return Author;
	}
	
	public String GetDescription() {
		return Description;
	}
}
