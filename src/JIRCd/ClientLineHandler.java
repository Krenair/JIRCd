package JIRCd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import JIRCd.Events.*;
import JIRCd.GUI.MainScreen;
import JIRCd.Plugins.PluginEventManager;

public class ClientLineHandler extends Thread{
	private Client SentBy;
	private String Line;
	public ClientLineHandler(Client SentBy){
		try {
			this.SentBy = SentBy;
			this.Line = SentBy.InFromClient.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		Main.RawIOLog.info("From " + SentBy.ThreadID + ": \"" + Line + "\"");
		String[] Split = Line.split(" ");
		if(Split[0].equalsIgnoreCase("NICK")) {
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " NICK :Not enough parameters");
				return;
			}
			
			boolean FoundName = false;
			for(Client Client : Main.Clients){
				if(Client.Nickname.equalsIgnoreCase(Split[1])){
					FoundName = true;
					break;
				}
			}
			if(FoundName){
				SentBy.SendLine(":" + Main.ServerName + " 433 " + SentBy.Nickname + " " + Split[1] + " :Nickname is already in use.");
				return;
			} else if(SentBy.HasAuthed) {
				List<Client> SendNotificationTo = new ArrayList<Client>();
				for(String ChannelName : SentBy.ChannelNames){
					for(Client Client : Main.Channels.get(ChannelName).Members){
						if(Client != SentBy && !SendNotificationTo.contains(Client)){
							SendNotificationTo.add(Client);
						}
					}
				}
				for (Client Client : SendNotificationTo){
					Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " NICK :" + Split[1]);
				}
				SentBy.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " NICK :" + Split[1]);
				SentBy.Nickname = Split[1];
				PluginEventManager.FireEvent(new ClientNicknameChangeEvent("OnClientNicknameChange", SentBy));
				return;
			}
			
			SentBy.Nickname = Split[1];
			SentBy.Auth++;
			
			if(SentBy.Auth == 2){
				SentBy.HasAuthed = true;
				SentBy.SendLine(":" + Main.ServerName + " 001 " + SentBy.Nickname + " :Welcome to the " + Main.NetworkName + " " + SentBy.Nickname);
				SentBy.SendLine(":" + Main.ServerName + " 002 " + SentBy.Nickname + " :Your host is " + Main.ServerName + "[" + Main.ServerSocketAddress + "], running version JIRCd " + Main.Version);
				SentBy.SendLine(":" + Main.ServerName + " 003 " + SentBy.Nickname + " :This server was created Mon Jan 17 2011 at 23:32:18 CST");
				/*
* canis.esper.net charybdis-3.3.0 DQRSZagiloswz CFILPQbcefgijklmnopqrstvz bkloveqjfI
* CHANTYPES=#~ EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFPcgimnpstz CHANLIMIT=#:30 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=EsperNet KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
* CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=30 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
* EXTBAN=$,acjorsxz WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
* There are 24 users and 4590 invisible on 8 servers
* 26 :IRC Operators online
* 1 :unknown connection(s)
* 2354 :channels formed
* I have 818 clients and 1 servers
* 818 1224 :Current local users 818, max 1224
* 4614 5403 :Current global users 4614, max 5403*/
				if(Main.MOTDLines != new String[]{""}){
					SentBy.SendLine(":" + Main.ServerName + " 375 " + SentBy.Nickname + " :- " + Main.ServerName + " Message of the Day - ");
					for(String Line : Main.MOTDLines){
						SentBy.SendLine(":" + Main.ServerName + " 372 " + SentBy.Nickname + " :" + Line);
					}
					SentBy.SendLine(":" + Main.ServerName + " 376 " + SentBy.Nickname + " :End of /MOTD command.");
				} else {
					SentBy.SendLine(":" + Main.ServerName + " 422 " + SentBy.Nickname + " :MOTD File is missing or blank.");
				}
				PluginEventManager.FireEvent(new ClientAuthEvent("OnClientAuth", SentBy));
			}
		} else if (Split[0].equalsIgnoreCase("USER")){
			if(SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 462 " + SentBy.Nickname + " :You may not reregister");
				return;
			}
			
			if(Split.length < 5){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " USER :Not enough parameters");
				return;
			}
			
			SentBy.Username = Split[1];
			String Realname = Line.substring(8 + Split[1].length() + Split[2].length() + Split[3].length());
			if(Character.toString(Realname.charAt(0)).equals(":"))
				Realname = Realname.substring(1);
			SentBy.Realname = Realname;
			SentBy.Auth++;
			
			if(SentBy.Auth == 2){
				SentBy.HasAuthed = true;
				SentBy.SendLine(":" + Main.ServerName + " 001 " + SentBy.Nickname + " :Welcome to the " + Main.NetworkName + " " + SentBy.Nickname);
				SentBy.SendLine(":" + Main.ServerName + " 002 " + SentBy.Nickname + " :Your host is " + Main.ServerName + "[" + Main.ServerSocketAddress + "], running version JIRCd " + Main.Version);
				SentBy.SendLine(":" + Main.ServerName + " 003 " + SentBy.Nickname + " :This server was created Mon Jan 17 2011 at 23:32:18 CST");
				/*
* canis.esper.net charybdis-3.3.0 DQRSZagiloswz CFILPQbcefgijklmnopqrstvz bkloveqjfI
* CHANTYPES=#~ EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFPcgimnpstz CHANLIMIT=#:30 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=EsperNet KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
* CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=30 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
* EXTBAN=$,acjorsxz WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
* There are 24 users and 4590 invisible on 8 servers
* 26 :IRC Operators online
* 1 :unknown connection(s)
* 2354 :channels formed
* I have 818 clients and 1 servers
* 818 1224 :Current local users 818, max 1224
* 4614 5403 :Current global users 4614, max 5403*/
				if(Main.MOTDLines != new String[]{""}){
					SentBy.SendLine(":" + Main.ServerName + " 375 " + SentBy.Nickname + " :- " + Main.ServerName + " Message of the Day - ");
					for(String Line : Main.MOTDLines){
						SentBy.SendLine(":" + Main.ServerName + " 372 " + SentBy.Nickname + " :" + Line);
					}
					SentBy.SendLine(":" + Main.ServerName + " 376 " + SentBy.Nickname + " :End of /MOTD command.");
				} else {
					SentBy.SendLine(":" + Main.ServerName + " 422 " + SentBy.Nickname + " :MOTD File is missing or blank.");
				}
				PluginEventManager.FireEvent(new ClientAuthEvent("OnClientAuth", SentBy));
			}
		} else if (Split[0].equalsIgnoreCase("JOIN")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length == 2){
				String ChannelName = Line.substring(5);
				if(!Character.toString(ChannelName.charAt(0)).equals("#")){
					SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + ChannelName + " :No such channel");
					return;
				}
				
				Channel Channel = null;
				if(!Main.Channels.containsKey(ChannelName)){
					Channel = new Channel(ChannelName, Main.ChannelNum + 1);
			    	Main.Channels.put(ChannelName, Channel);
			    	Main.ChannelNum++;
			    	Main.Channels.get(ChannelName).Owner = SentBy;
			    	Main.Channels.get(ChannelName).Ops.add(SentBy);
				}
				
				if(Channel == null){
					Channel = Main.Channels.get(ChannelName); 
				}
				
				if(Channel.IsInviteOnly && !(Channel.Invited.contains(this) || SentBy.IsOperator)){
					SentBy.SendLine(":" + Main.ServerName + " 473 " + SentBy.Nickname + " " + ChannelName + " :Cannot join channel (+i)");
					return;
				}
				
				Main.Channels.get(ChannelName).Members.add(SentBy);
				
				for(Client Client : Channel.Members){
					Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " JOIN :" + ChannelName);
				}
				PluginEventManager.FireEvent(new ClientJoinChannelEvent("OnClientJoinChannel", SentBy, Channel));
				SentBy.ChannelNames.add(ChannelName);
				MainScreen.ChannelList.setListData(Main.Channels.keySet().toArray(new String[]{}));
				/*SentBy.SendLine(":" + Main.ServerName + " MODE " + Split[1] + " +oq " + SentBy.Nickname + " " + SentBy.Nickname);
				
				StringBuffer NameBuffer = new StringBuffer(":" + Main.ServerName + " 353 " + SentBy.Nickname + " @ " + Split[1] + " :");
				for(Client Client : Main.Channels.get(Split[1]).Members){
					String ModeSymbol = "";
					if (Channel.Owner == Client) {
						ModeSymbol = "@";
					} else if (Channel.ProtectedOps.contains(Client)) {
						ModeSymbol = "@";
					} else if (Channel.Ops.contains(Client)) {
						ModeSymbol = "@";
					} else if (Channel.HalfOps.contains(Client)) {
						ModeSymbol = "@";
					} else if (Channel.Voices.contains(Client)) {
						ModeSymbol = "+";
					}
					NameBuffer.append(ModeSymbol + Client.Nickname + " ");
				}
				SentBy.SendLine(NameBuffer.toString());
				SentBy.SendLine(":" + Main.ServerName + " 366 " + SentBy.Nickname + " " + Split[1] + " :End of /NAMES list.");*/
			}
		} else if (Split[0].equalsIgnoreCase("PART")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered.");
				return;
			}
			
			if(Split.length >= 2){
				String ChannelName = Line.substring(5);
				if(ChannelName.indexOf(" :") != -1)
					ChannelName = ChannelName.substring(0, ChannelName.indexOf(" :"));
				
				if(!Main.Channels.containsKey(ChannelName)){
					SentBy.SendLine(":" + Main.ServerName + " 442 " + SentBy.Nickname + " " + ChannelName + " :You're not on that channel");
					return;
				}
				
				for(Client Client : Main.Channels.get(ChannelName).Members){
					Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " PART :" + Line.substring(5));
				}
				
				Main.Channels.get(ChannelName).RemoveMember(SentBy);
				
				SentBy.ChannelNames.remove(ChannelName);
				PluginEventManager.FireEvent(new ClientPartChannelEvent("OnClientPartChannel", SentBy, Main.Channels.get(ChannelName)));
			}
		} else if (Split[0].equalsIgnoreCase("PING")) {
			SentBy.SendLine(":" + Main.ServerName + " PONG " + Main.ServerName + " :" + Line.substring(5));
		} else if (Split[0].equalsIgnoreCase("MODE")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " MODE :Not enough parameters");
			}
			
			if(Split.length == 2 && Character.toString(Split[1].charAt(0)).equals("#")){
				try {
					SentBy.SendLine(":" + Main.ServerName + " 324 " + SentBy.Nickname + " " + Split[1] + " " + Main.Channels.get(Split[1]).GetMode());
					return;
				} catch (NullPointerException e){
					SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1] + " :No such channel");
					return;
				}
			} else if(Split.length > 2){
				if(Character.toString(Split[1].charAt(0)).equals("#")){
					 if(!Character.toString(Split[2].charAt(0)).equals("+") && !Character.toString(Split[2].charAt(0)).equals("-")){
						 SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
						 return;
					 }
					 
					 if(!Main.Channels.containsKey(Split[1])){
						 SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1] + " :No such channel");
					 }
					 
					 String Current = "";
					 StringBuffer AddBuffer = new StringBuffer("");
					 StringBuffer RemoveBuffer = new StringBuffer("");
					 for(int I = 0; I < Line.length(); I++){
						 if(Character.toString(Line.charAt(I)).equals("+")){
							 Current = "+";
						 } else if(Character.toString(Line.charAt(I)).equals("-")){
							 Current = "-";
						 } else if(Current.equals("+")){
							 AddBuffer.append(Line.charAt(I));
						 } else if(Current.equals("-")){
							 RemoveBuffer.append(Line.charAt(I));
						 }
					 }
					 
					 String Add = AddBuffer.toString();
					 String Remove = RemoveBuffer.toString();
					 
					 String[] ValidChanModes = {"t", "C", "c", "k", "m", "M", "s", "i",
							 					/*"b", "e", "q", "a", "o", "h", "v"*/};
						
					 
					 boolean KeepGoing = true;
					 //check addition modes
					 for(int I = 0; I < Add.length(); I++){
						 if(!Main.EqualsOneOf(Character.toString(Add.charAt(I)),ValidChanModes)){
							 SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
							 KeepGoing = false;
							 break;
						 }
					 }
					 
					 if(!KeepGoing){
						 return;
					 }
					 
					 //check removal modes
					 for(int I = 0; I < Remove.length(); I++){
						 if(!Main.EqualsOneOf(Character.toString(Remove.charAt(I)),ValidChanModes)){
							 SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
							 KeepGoing = false;
							 break;
						 }
					 }
					 
					 if(!KeepGoing){
						 return;
					 }
					 
					 PluginEventManager.FireEvent(new ClientChangeChannelModeEvent("OnChannelModeChange", SentBy,  Main.Channels.get(Split[1]), Add, Remove));
					 
					 for(int I = 0; I < Add.length(); I++){
						 if(Character.toString(Add.charAt(I)).equals("t")){
							 Main.Channels.get(Split[1]).OnlyOpsCanChangeTopic = true;
						 } else if(Character.toString(Add.charAt(I)).equals("C")){
							 Main.Channels.get(Split[1]).NoChannelCtcp = true;
						 } else if(Character.toString(Add.charAt(I)).equals("c")){
							 Main.Channels.get(Split[1]).NoChannelColours = true;
						 } else if(Character.toString(Add.charAt(I)).equals("k")){
							 Main.Channels.get(Split[1]).RequiresKey = true;
						 } else if(Character.toString(Add.charAt(I)).equals("m")){
							 Main.Channels.get(Split[1]).IsModerated = true;
						 } else if(Character.toString(Add.charAt(I)).equals("M")){
							 Main.Channels.get(Split[1]).IsModeratedForNonAuthed = true;
						 } else if(Character.toString(Add.charAt(I)).equals("s")){
							 Main.Channels.get(Split[1]).IsSecret = true;
						 } else if(Character.toString(Add.charAt(I)).equals("i")){
							 Main.Channels.get(Split[1]).IsInviteOnly = true;
						 } /*else if(Character.toString(Add.charAt(I)).equals("q")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("a")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("o")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("h")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("v")){
							 //TODO
						 }*/
					 }
					 
					 for(int I = 0; I < Remove.length(); I++){
						 if(Character.toString(Add.charAt(I)).equals("t")){
							 Main.Channels.get(Split[1]).OnlyOpsCanChangeTopic = false;
						 } else if(Character.toString(Add.charAt(I)).equals("C")){
							 Main.Channels.get(Split[1]).NoChannelCtcp = false;
						 } else if(Character.toString(Add.charAt(I)).equals("c")){
							 Main.Channels.get(Split[1]).NoChannelColours = false;
						 } else if(Character.toString(Add.charAt(I)).equals("k")){
							 Main.Channels.get(Split[1]).RequiresKey = false;
						 } else if(Character.toString(Add.charAt(I)).equals("m")){
							 Main.Channels.get(Split[1]).IsModerated = false;
						 } else if(Character.toString(Add.charAt(I)).equals("M")){
							 Main.Channels.get(Split[1]).IsModeratedForNonAuthed = false;
						 } else if(Character.toString(Add.charAt(I)).equals("s")){
							 Main.Channels.get(Split[1]).IsSecret = false;
						 } else if(Character.toString(Add.charAt(I)).equals("i")){
							 Main.Channels.get(Split[1]).IsInviteOnly = false;
						 } /*else if(Character.toString(Add.charAt(I)).equals("q")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("a")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("o")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("h")){
							 //TODO
						 } else if(Character.toString(Add.charAt(I)).equals("v")){
							 //TODO
						 }*/
					 }
					 
					 for(Client Client : Main.Channels.get(Split[1]).Members){
						 Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " MODE " + Split[1] + " " + Split[2]);
					 }
				}
				else{
					if(!Character.toString(Split[2].charAt(0)).equals("+") && !Character.toString(Split[2].charAt(0)).equals("-")){
						SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
						return;
					}
					
					if(SentBy.Nickname.equals(Split[1]) || SentBy.IsOperator){
						Client TargetClient = null;
						for(Client Client : Main.Clients){
							if(Client.Nickname.equals(Split[1])){
								TargetClient = Client;
							}
						}
						
						if(TargetClient == null){
							SentBy.SendLine(":" + Main.ServerName + " 401 " + SentBy.Nickname + " " + Split[1] + " :No such nick/channel");
							return;
						}
						
						String Current = "";
						StringBuffer AddBuffer = new StringBuffer("");
						StringBuffer RemoveBuffer = new StringBuffer("");
						for(int I = 0; I < Line.length(); I++){
							if(Character.toString(Line.charAt(I)).equals("+")){
								Current = "+";
							}
							else if(Character.toString(Line.charAt(I)).equals("-")){
								Current = "-";
							}
							else if(Current.equals("+")){
								AddBuffer.append(Line.charAt(I));
							}
							else if(Current.equals("-")){
								RemoveBuffer.append(Line.charAt(I));
							}
						}
						 
						 String Add = AddBuffer.toString();
						 String Remove = RemoveBuffer.toString();
						 
						 String[] ValidUserModes = {"o", "B", "r", "s"};
						 
						 boolean KeepGoing = true;
						 //check addition modes
						 for(int I = 0; I < Add.length(); I++){
							 if(!Main.EqualsOneOf(Character.toString(Add.charAt(I)),ValidUserModes)){
								 SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
								 KeepGoing = false;
								 break;
							 }
						 }
						 
						 if(!KeepGoing){
							 return;
						 }
						 
						 //check removal modes
						 for(int I = 0; I < Remove.length(); I++){
							 if(!Main.EqualsOneOf(Character.toString(Remove.charAt(I)),ValidUserModes)){
								 SentBy.SendLine(":" + Main.ServerName + " 501 " + SentBy.Nickname + " :Unknown MODE flag");
								 KeepGoing = false;
								 break;
							 }
						 }
						 
						 if(!KeepGoing){
							 return;
						 }
						 
						 for(int I = 0; I < Add.length(); I++){
							 if(Character.toString(Add.charAt(I)).equals("o")){
								 if(SentBy.IsOperator){
									 TargetClient.IsOperator = true;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
									 KeepGoing = false;
									 break;
								 }
							 }							 
							 else if(Character.toString(Add.charAt(I)).equals("B")){
								 TargetClient.IsBot = true;
							 }
							 else if(Character.toString(Add.charAt(I)).equals("r")){
								 if(SentBy.IsOperator){
									 TargetClient.IsAuthed = true;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
									 KeepGoing = false;
									 break;
								 }
							 }
							 else if(Character.toString(Add.charAt(I)).equals("s")){
								 if(SentBy.IsOperator){
									 TargetClient.IsService = true;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
									 KeepGoing = false;
									 break;
								 }
							 }
						 }
						 
						 if(!KeepGoing){
							 return;
						 }
						 
						 for(int I = 0; I < Remove.length(); I++){
							 if(Character.toString(Add.charAt(I)).equals("o")){
								 if(SentBy.IsOperator){
									 TargetClient.IsOperator = false;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
									 KeepGoing = false;
									 break;
								 }
							 }							 
							 else if(Character.toString(Add.charAt(I)).equals("B")){
								 TargetClient.IsBot = false;
							 }
							 else if(Character.toString(Add.charAt(I)).equals("r")){
								 if(SentBy.IsOperator){
									 TargetClient.IsAuthed = false;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
								 }
							 }
							 else if(Character.toString(Add.charAt(I)).equals("s")){
								 if(SentBy.IsOperator){
									 TargetClient.IsService = false;
								 }
								 else{
									 SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :You're not oper.");
								 }
							 }
						 }
						 
						 if(!KeepGoing){
							 return;
						 }
						 
						 TargetClient.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " MODE " + Split[1] + " " + Split[2]);
					} else{
						SentBy.SendLine(":" + Main.ServerName + " 481 " + SentBy.Nickname + " :Permission Denied- You're not an IRC operator");
					}
				}
			}
		} else if (Split[0].equalsIgnoreCase("WHOIS")){
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " WHOIS :Not enough parameters");
				return;
			}
			
			boolean Found = false;
			Client[] ClientList = Main.Clients.toArray(new Client[]{});
			for(Client Client : ClientList){
				if(Client.Nickname.equals(Split[1])){
					SentBy.SendLine(":" + Main.ServerName + " 311 " + SentBy.Nickname + " " + Client.Nickname + " " + Client.Username + " " + Client.Hostname + " * :" + Client.Realname);
					String ChannelList = ":" + Main.ServerName + " 319 " + Client.Nickname + " :";
					for(String ChannelName : Client.ChannelNames){
						Channel Channel = Main.Channels.get(ChannelName);
						String ModeSymbol = "";
						if (Channel.Owner == Client) {
							ModeSymbol = "~";
						} else if (Channel.ProtectedOps.contains(Client)) {
							ModeSymbol = "&";
						} else if (Channel.Ops.contains(Client)) {
							ModeSymbol = "@";
						} else if (Channel.HalfOps.contains(Client)) {
							ModeSymbol = "%";
						} else if (Channel.Voices.contains(Client)) {
							ModeSymbol = "+";
						}
						ChannelList += ModeSymbol + ChannelName + " ";
					}
					SentBy.SendLine(ChannelList.trim());
					SentBy.SendLine(":" + Main.ServerName + " 318 " + SentBy.Nickname + " " + Client.Nickname + " :End of /WHOIS list.");
					Found = true;
					break;
				}
			}
			if(!Found){
				SentBy.SendLine(":" + Main.ServerName + " 401 " + SentBy.Nickname + " " + Split[1] + " :No such nick/channel");
				SentBy.SendLine(":" + Main.ServerName + " 318 " + SentBy.Nickname + " " + Split[1] + " :End of /WHOIS list.");
			}
		} else if (Split[0].equalsIgnoreCase("WHO")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " WHO :Not enough parameters");
				return;
			}
			
			if(!Main.Channels.containsKey(Split[1])){
				SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1]);
				return;
			}
			
			Channel Channel = Main.Channels.get(Split[1]);
			for(Client Client : Main.Channels.get(Split[1]).Members) {
				String ModeSymbol = "";
				/*if (Channel.Owner == Client) {
					ModeSymbol = "~";
				} else if (Channel.ProtectedOps.contains(Client)) {
					ModeSymbol = "&";
				} else if (Channel.Ops.contains(Client)) {
					ModeSymbol = "@";
				} else if (Channel.HalfOps.contains(Client)) {
					ModeSymbol = "%";
				} else if (Channel.Voices.contains(Client)) {
					ModeSymbol = "+";
				}*/
				if (Channel.Owner == Client) {
					ModeSymbol = "@";
				} else if (Channel.ProtectedOps.contains(Client)) {
					ModeSymbol = "@";
				} else if (Channel.Ops.contains(Client)) {
					ModeSymbol = "@";
				} else if (Channel.HalfOps.contains(Client)) {
					ModeSymbol = "@";
				} else if (Channel.Voices.contains(Client)) {
					ModeSymbol = "+";
				}
				
				if(Split.length > 2 && Split[2].equals("o")){
					if(ModeSymbol.equals("@") || ModeSymbol.equals("&") || ModeSymbol.equals("~")){
						SentBy.SendLine(":" + Main.ServerName + " 352 " + SentBy.Nickname + " " + Split[1] + " " + Client.Username + " " + Client.Hostname + " " + Main.ServerName + " " + Client.Nickname + " H" + ModeSymbol + " :0 " + Client.Realname);
					}
				} else {
					SentBy.SendLine(":" + Main.ServerName + " 352 " + SentBy.Nickname + " " + Split[1] + " " + Client.Username + " " + Client.Hostname + " " + Main.ServerName + " " + Client.Nickname + " H" + ModeSymbol + " :0 " + Client.Realname);
				}
			}
			SentBy.SendLine(":" + Main.ServerName + " 315 " + SentBy.Nickname + " " + Split[1] + " :End of /WHO list");
		} else if (Split[0].equalsIgnoreCase("NAMES")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " NAMES :Not enough parameters");  
				return;
			}
			
			if(!Main.Channels.containsKey(Split[1])){
				SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1] + " :No such channel");
				return;
			}
			
			for(Client Client : Main.Channels.get(Split[1]).Members) {
				SentBy.SendLine(":" + Main.ServerName + " 353 " + SentBy.Nickname + " @ " + Split[1] + " :@" + Client.Nickname);
			}
			SentBy.SendLine(":" + Main.ServerName + " 366 " + SentBy.Nickname + " " + Split[1] + " :End of /NAMES list.");
		} else if (Split[0].equalsIgnoreCase("PRIVMSG")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			Line = Line.substring(8);
			StringBuffer Buffer = new StringBuffer("");
			for(int I = 0; I <= Line.length(); I++){
				if(Character.toString(Line.charAt(I)).equals(" ")){
					break;
				}
				Buffer.append(Character.toString(Line.charAt(I)));
			}
			String To = Buffer.toString();
			String Message = Line.substring(To.length() + 1);
			
			if(Character.toString(Message.charAt(0)).equals(":")){
				Message = Message.substring(1);
			}
			
			if(Character.toString(To.charAt(0)).equals("#")){
				if(Main.Channels.get(To).Members.contains(this)){
					for(Client Client : Main.Channels.get(To).Members){
						if(Client != SentBy){
							Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " PRIVMSG " + To + " :" + Message);
						}
					}
					PluginEventManager.FireEvent(new ClientSendChannelMessageEvent("OnClientSendChannelMessage", SentBy, Main.Channels.get(To), Message));
				}
				else{
					SentBy.SendLine(":" + Main.ServerName + " 442 " + SentBy.Nickname + " " + To + " :You're not on that channel");
				}
			} else{
				for(Client Client : Main.Clients){
					if(Client.Nickname.equals(To)){
						Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " PRIVMSG " + To + " :" + Message);
					}
					PluginEventManager.FireEvent(new ClientSendPrivateMessageEvent("OnClientSendPrivateMessage", SentBy, Client, Message));
				}
			}
		} else if (Split[0].equalsIgnoreCase("NOTICE")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			Line = Line.substring(8);
			StringBuffer Buffer = new StringBuffer("");
			for(int I = 0; I <= Line.length(); I++){
				if(Character.toString(Line.charAt(I)).equals(" ")){
					break;
				}
				Buffer.append(Character.toString(Line.charAt(I)));
			}
			String To = Buffer.toString();
			String Message = Line.substring(To.length() + 1);
			
			if(Character.toString(Message.charAt(0)).equals(":")){
				Message = Message.substring(1);
			}
			
			if(Character.toString(To.charAt(0)).equals("#")){
				if(Main.Channels.get(To).Members.contains(this)){
					for(Client Client : Main.Channels.get(To).Members){
						if(Client != SentBy){
							Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " NOTICE " + To + " :" + Message);
						}
					}
					PluginEventManager.FireEvent(new ClientSendChannelMessageEvent("OnClientSendChannelNotice", SentBy, Main.Channels.get(To), Message));
				}
				else{
					SentBy.SendLine(":" + Main.ServerName + " 442 " + SentBy.Nickname + " " + To + " :You're not on that channel");
				}
			} else{
				for(Client Client : Main.Clients){
					if(Client.Nickname.equals(To)){
						Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " NOTICE " + To + " :" + Message);
					}
					PluginEventManager.FireEvent(new ClientSendPrivateMessageEvent("OnClientSendPrivateNotice", SentBy, Client, Message));
				}
			}
		} else if (Split[0].equalsIgnoreCase("TOPIC")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " TOPIC :Not enough parameters");
				return;
			}
			
			if(!Main.Channels.containsKey(Split[1])){
				SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1] + " :No such channel");
				return;
			}
			
			if(Split.length > 2){
				StringBuffer Buffer = new StringBuffer("");
				for(int i = 2; i <= Split.length; i++){
					Buffer.append(Split[i] + " ");
				}
				String FinalTopic = Buffer.toString().trim();
				Main.Channels.get(Split[1]).Topic = FinalTopic;
				PluginEventManager.FireEvent(new ClientChangeChannelTopicEvent("OnClientChangeChannelTopic", SentBy, Main.Channels.get(Split[1]), FinalTopic));
			}
			SentBy.SendLine(":" + Main.ServerName + " 332 " + SentBy.Nickname + " " + Split[1] + " :" + Main.Channels.get(Split[1]).Topic);
			return;
		} else if (Split[0].equalsIgnoreCase("QUIT")) {
			int index = 5;
			if (Character.toString(Line.charAt(6)).equals(":")) {
				index++;
			}
			SentBy.CleanDisconnect(Line.substring(index));
		} else if (Split[0].equalsIgnoreCase("LIST")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			SentBy.SendLine(":" + Main.ServerName + " 321 " + SentBy.Nickname + " Channel :Users Name");
			for(int I = 0; I < Main.Channels.size(); I++){
				if(!Main.Channels.get(I).IsSecret || Main.Channels.get(I).Members.contains(this)){
					SentBy.SendLine(":" + Main.ServerName + " 322 " + SentBy.Nickname + " " + Main.Channels.get(I).Name + " " + Main.Channels.get(I).Members.size() + " :" + Main.Channels.get(I).Topic);						
				}
			}
			SentBy.SendLine(":" + Main.ServerName + " 323 " + SentBy.Nickname + " :End of /LIST");
		} else if (Split[0].equalsIgnoreCase("VERSION")) {
			SentBy.SendLine(":" + Main.ServerName + " 351 " + SentBy.Nickname + " JIRCd " + Main.Version + ".0 " + Main.ServerName);
		} else if (Split[0].equalsIgnoreCase("KICK")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " KICK :Not enough parameters");
				return;
			}
			
			if(!Main.Channels.containsKey(Split[1])){
				SentBy.SendLine(":" + Main.ServerName + " 403 " + SentBy.Nickname + " " + Split[1] + " :No such channel");
				return;
			}
			
			if(!Main.Channels.get(Split[1]).Members.contains(SentBy)){
				SentBy.SendLine(":" + Main.ServerName + " 442 " + SentBy.Nickname + " " + Split[1] + " :You're not on that channel");
				return;
			}
			
			if(!Main.Channels.get(Split[1]).Ops.contains(SentBy)){
				SentBy.SendLine(":" + Main.ServerName + " 482 " + SentBy.Nickname + " " + Split[1] + " :You're not channel operator");
				return;
			}
			
			String KickMessage = ":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " KICK " + Split[1] + " " + Split[2];
			if(Split.length == 3){
				KickMessage += " " + Split[3];
			}
			
			for(Client Client : Main.Channels.get(Split[1]).Members){
				Client.SendLine(KickMessage);
				Main.Channels.get(Split[1]).Members.remove(Client);
			}
			
			if(Main.Channels.get(Split[1]).Members.size() == 0)
				Main.Channels.remove(Split[1]);
		} else if (Split[0].equalsIgnoreCase("INVITE")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 2){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " INVITE :Not enough parameters");
				return;
			}
			
			if(!Main.Channels.containsKey(Split[2]) && !Main.Channels.get(Split[2]).Members.contains(this)) {
		    	Main.Channels.put(Split[2], new Channel(Split[2], Main.ChannelNum + 1));
		    	Main.ChannelNum++;
			}
			
			Client TargetClient = null;
			boolean Found = false;
			for(Client Client : Main.Clients){
				if(Client.Nickname.equals(Split[1])){
					TargetClient = Client;
					Found = true;
					break;
				}
			}
			if(!Found || TargetClient == null){
				SentBy.SendLine(":" + Main.ServerName + " 401 " + SentBy.Nickname + " " + Split[1] + " :No such nick/channel");
				return;
			}
			
			Channel TargetChannel = Main.Channels.get(Split[2]);
			TargetChannel.Invited.add(TargetClient);
			List<Client> ChannelNotifications = new ArrayList<Client>();
			for(Client Client : TargetChannel.Members){
				if(Client != SentBy && !ChannelNotifications.contains(Client)){
					ChannelNotifications.add(Client);
				}
			}
			for (Client Client : ChannelNotifications){
				Client.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " INVITE " + TargetClient.Nickname + " " + TargetChannel.Name);
			}
			SentBy.SendLine(":" + Main.ServerName + " 341 " + TargetChannel.Name + " " + TargetClient.Nickname);
			TargetClient.SendLine(":" + SentBy.Nickname + "!" + SentBy.Username + "@" + SentBy.Hostname + " INVITE " + TargetClient.Nickname + " " + TargetChannel.Name );
			return;
		} else if (Split[0].equalsIgnoreCase("OPER")) {
			if(!SentBy.HasAuthed){
				SentBy.SendLine(":" + Main.ServerName + " 451 " + SentBy.Nickname + " :You have not registered");
				return;
			}
			
			if(Split.length < 3){
				SentBy.SendLine(":" + Main.ServerName + " 461 " + SentBy.Nickname + " OPER :Not enough parameters");
				return;
			}
			
			boolean Found = false;
			try{
				DataInputStream In = new DataInputStream(new FileInputStream("opers"));
				BufferedReader Br = new BufferedReader(new InputStreamReader(In));
				String StrLine = "";
				while ((StrLine = Br.readLine()) != null && !Found){
				    MessageDigest User_digest = MessageDigest.getInstance("MD5");
				    User_digest.update(Split[1].getBytes());
				    String User_hash = User_digest.digest().toString();
				    
				    MessageDigest Pass_digest = MessageDigest.getInstance("MD5");
				    Pass_digest.update(Split[2].getBytes());
				    String Pass_hash = Pass_digest.digest().toString();
				    
					if(StrLine.equals(User_hash + "	" + Pass_hash)){
						Found = true;
					}
				}
				In.close();
			} catch (IOException e){
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e){
				e.printStackTrace();
			}
			
			if(Found){
				SentBy.IsOperator = true;
				SentBy.SendLine(":" + Main.ServerName + " 381 " + SentBy.Nickname + " :You are now an IRC operator");
			} else {
				SentBy.SendLine(":" + Main.ServerName + " 491 " + SentBy.Nickname + " :Could not find a matching oper username/password.");
			}
		} else if (Split[0].equalsIgnoreCase("SUMMON")) {
			SentBy.SendLine(":" + Main.ServerName + " 445 " + SentBy.Nickname + " :SUMMON has been disabled");
		} else if (Split[0].equalsIgnoreCase("USERS")) {
			SentBy.SendLine(":" + Main.ServerName + " 446 " + SentBy.Nickname + " :USERS has been disabled");
		} else if (Split[0].equalsIgnoreCase("CAP")) {
			if (!Main.EqualsOneOf(Split[1], new String[]{"LS", "LIST", "REQ", "ACK", "NAK", "CLEAR", "END"})) {
				
			}
		} else {
			SentBy.SendLine(":" + Main.ServerName + " 421 " + SentBy.Nickname + " :Unknown command");
			Main.MainLog.info("WARNING: Unknown command was used: Line: " + Line);
		}
	}
}
