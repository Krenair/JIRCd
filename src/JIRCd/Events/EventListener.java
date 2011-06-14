package JIRCd.Events;


public class EventListener {
	public void OnClientAuth(Event Event) {}
	public void OnClientDisconnect(Event Event) {}
	public void OnClientModeChange(Event Event) {}
	public void OnClientNicknameChange(Event Event) {}
	public void OnClientSendPrivateMessage(Event Event) {}
	public void OnClientSendPrivateNotice(Event Event) {}
	public void OnClientJoinChannel(Event Event) {}
	public void OnClientPartChannel(Event Event) {}
	public void OnClientSendChannelMessage(Event Event) {}
	public void OnClientSendChannelNotice(Event Event) {}
	public void OnClientChangeChannelTopic(Event Event) {}
	public void OnClientChangeChannelMode(Event Event) {}
}