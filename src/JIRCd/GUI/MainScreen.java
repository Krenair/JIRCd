package JIRCd.GUI;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import net.miginfocom.swing.MigLayout;

import JIRCd.Channel;
import JIRCd.Client;
import JIRCd.Main;

public class MainScreen {

	private JFrame frame;
	private JButton btnShutDown;
	public static JList ClientList;
	public static JList ChannelList;
	private JLabel lblClientsOnline;
	private JLabel lblChannelsFormed;
	private JButton btnKillClient;
	private JButton btnDisbandChannel;
	private JButton btnSendMessage;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen window = new MainScreen();
					window.frame.setVisible(true);
					window.frame.setTitle("JIRCd");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainScreen() {
		frame = new JFrame();
		frame.setBounds(100, 100, 925, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[175px:n:175px][][175px:n:175px][175px:n:175px,grow][175px:n:175px][][175px:n:175px,grow]", "[25px][][grow][][][][][][][][][][][]"));
		
		lblClientsOnline = new JLabel("Clients online:");
		frame.getContentPane().add(lblClientsOnline, "cell 0 0");
		
		lblChannelsFormed = new JLabel("Channels formed:");
		frame.getContentPane().add(lblChannelsFormed, "cell 6 0");
		
		ClientList = new JList();
		frame.getContentPane().add(ClientList, "cell 0 1 1 13,grow");
		
		ChannelList = new JList();
		frame.getContentPane().add(ChannelList, "cell 6 1 1 12,grow");
		
		btnKillClient = new JButton("Kill Client");
		frame.getContentPane().add(btnKillClient, "cell 2 2,alignx center,aligny center");
		btnKillClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ClientList.getSelectedValue() == null) {
					new NoClientsSelectedError(frame);
					return;
				}
				
				Client[] FullClientList = Main.Clients.toArray(new Client[]{});
				for(Object ClientName : ClientList.getSelectedValues()) {
					for (Client Client : FullClientList) {
						if(Client.Nickname.equals(ClientName)) {
							Client.CleanDisconnect("Kill issued by console.");
						}
					}
				}
				
				List<String> ClientNames = new ArrayList<String>();
				for (Client Client : FullClientList) {
					ClientNames.add(Client.Nickname);
				}
				MainScreen.ClientList.setListData(ClientNames.toArray(new String[]{}));
				MainScreen.ChannelList.setListData(Main.Channels.keySet().toArray(new String[]{}));
			}
		});

		
		btnShutDown = new JButton("Shut down");
		frame.getContentPane().add(btnShutDown, "cell 3 7,alignx center,aligny center");
		btnShutDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		btnDisbandChannel = new JButton("Disband Channel");
		frame.getContentPane().add(btnDisbandChannel, "cell 4 2,alignx center,aligny center");
		btnDisbandChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ChannelList.getSelectedValue() == null) {
					new NoChannelsSelectedError(frame);
					return;
				}
				
				for(Object ChannelName : ChannelList.getSelectedValues()) {
					Channel Channel = Main.Channels.get(ChannelName);
					for (Client Client : Channel.Members){
						Client.SendLine(":" + Main.ServerName + " KICK " + ChannelName + " " + Client.Nickname + " :Disbanding channel.");
						Channel.Members.remove(Client);
					}
				}
				
				try {
					MainScreen.ChannelList.setListData(Main.Channels.keySet().toArray(new String[]{}));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
		
		btnSendMessage = new JButton("Send message");
		frame.getContentPane().add(btnSendMessage, "cell 3 11,alignx center,aligny center");
		btnSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!SendMessageForm.IsOpen)
					SendMessageForm.main();
			}
		});
	}
}
