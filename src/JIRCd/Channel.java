package JIRCd;
import java.util.ArrayList;
import java.util.List;

import JIRCd.GUI.MainScreen;

public class Channel{
	public String Name = "";
	public int ID = 0;
	public Client Owner;
	public List<Client> ProtectedOps = new ArrayList<Client>();
	public List<Client> Ops = new ArrayList<Client>();
	public List<Client> HalfOps = new ArrayList<Client>();
	public List<Client> Voices = new ArrayList<Client>();
	public List<Client> Members = new ArrayList<Client>();
	public List<Client> Invited = new ArrayList<Client>();
	public String Topic = "";
	//public HashMap<Client, String> members = new HashMap<Client, String>();
	
	public boolean OnlyOpsCanChangeTopic = true;
	public boolean NoChannelCtcp = false;
	public boolean NoChannelColours = false;
	public boolean RequiresKey = false;
	public boolean IsModerated = false;
	public boolean IsModeratedForNonAuthed = false;
	public boolean IsSecret = false;
	public boolean IsInviteOnly = false;
	
	public int Limit = 0;
	public String Key = "";
	
	public String GetMode() {
		StringBuffer output = new StringBuffer("+");
		if(this.OnlyOpsCanChangeTopic)
			output.append("t");
		
		if(this.NoChannelCtcp)
			output.append("C");
		
		if(this.NoChannelColours)
			output.append("c");
		
		if(this.RequiresKey)
			output.append("k");
		
		if(this.IsModerated)
			output.append("m");
		
		if(this.IsModeratedForNonAuthed)
			output.append("M");
		
		if(this.IsSecret)
			output.append("s");
		
		if(this.IsInviteOnly)
			output.append("i");
		
		return output.toString();
	}
	
	public Channel(String name, int num) {
		this.Name = name;
		this.ID = num;
	}
	
	public void RemoveMember(Client Client) {
		if(Members.contains(Client))
			Members.remove(Client);
		
		if (Members.size() == 0) {
			Main.Channels.remove(Name);
			MainScreen.ChannelList.setListData(Main.Channels.keySet().toArray(new String[]{}));
		}
	}
}