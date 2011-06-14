package JIRCd.GUI;

import JIRCd.Channel;
import JIRCd.Client;
import JIRCd.Main;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class SendMessageForm {
	
	public static boolean IsOpen = false;

	private JFrame frmJircdSend;
	private JButton btnSubmit = new JButton("Submit");
	private JComboBox ChannelList = new JComboBox();
	private JComboBox ClientList = new JComboBox();
	private JRadioButton rdbtnClientRadio = new JRadioButton("Client");
	private JRadioButton rdbtnChannelRadio = new JRadioButton("Channel");
	private JTextArea textArea = new JTextArea();

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new SendMessageForm().frmJircdSend.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		IsOpen = true;
	}

	/**
	 * Create the application.
	 */
	public SendMessageForm() {
		frmJircdSend = new JFrame();
		frmJircdSend.setTitle("JIRCd - Send Message");
		frmJircdSend.setBounds(100, 100, 450, 300);
		frmJircdSend.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmJircdSend.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}

			public void windowClosed(WindowEvent e) {
				IsOpen = false;
			}

			public void windowClosing(WindowEvent e) {}

			public void windowDeactivated(WindowEvent e) {}

			public void windowDeiconified(WindowEvent e) {}

			public void windowIconified(WindowEvent e) {}

			public void windowOpened(WindowEvent e) {}
		});
		frmJircdSend.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min(50dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(27dlu;default)"),}));
		
		frmJircdSend.getContentPane().add(rdbtnChannelRadio, "4, 2");
		rdbtnChannelRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnClientRadio.setSelected(false);
				ChannelList.setEnabled(true);
				ClientList.setEnabled(false);
				btnSubmit.setEnabled(true);
			}
		});
		
		Component verticalStrut = Box.createVerticalStrut(20);
		frmJircdSend.getContentPane().add(verticalStrut, "6, 1, 1, 3");
		
		frmJircdSend.getContentPane().add(rdbtnClientRadio, "8, 2");
		rdbtnClientRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnChannelRadio.setSelected(false);
				ChannelList.setEnabled(false);
				ClientList.setEnabled(true);
				btnSubmit.setEnabled(true);
			}
		});
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		frmJircdSend.getContentPane().add(horizontalStrut, "1, 4, 8, 1");
		
		ChannelList.setEnabled(false);
		frmJircdSend.getContentPane().add(ChannelList, "4, 6, default, center");
		for (String ChannelName : Main.Channels.keySet()) {
			ChannelList.addItem(ChannelName);
		}
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		frmJircdSend.getContentPane().add(verticalStrut_1, "6, 6");
		
		ClientList.setEnabled(false);
		frmJircdSend.getContentPane().add(ClientList, "8, 6, fill, default");
		Client[] CL = Main.Clients.toArray(new Client[]{});
		for (Client Client : CL) {
			ClientList.addItem(Client.Nickname);
		}
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		frmJircdSend.getContentPane().add(horizontalStrut_1, "1, 8, 8, 1");
		
		frmJircdSend.getContentPane().add(textArea, "4, 10, 5, 1, fill, fill");
		
		btnSubmit.setEnabled(false);
		frmJircdSend.getContentPane().add(btnSubmit, "4, 12, 5, 1, center, center");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!ClientList.isEnabled() && !ChannelList.isEnabled()) { //If neither is enabled.
					return;
				}
				
				if (ChannelList.isEnabled()) {
					if(ClientList.getSelectedObjects() == null) { //If list is enabled but nothing is selected.
						new NoClientsSelectedError(frmJircdSend);
						return;
					}
				} else if (ClientList.isEnabled()) {
					if(ChannelList.getSelectedObjects() == null) {
						new NoChannelsSelectedError(frmJircdSend);
						return;
					}
				}

				String[] Lines;
				try {
					Lines = textArea.getText().split("\r\n");
				} catch (NullPointerException e) {
					return;
				}
				if (Lines.length == 0 && Lines[0].length() == 0) {
					return;
				}
				
				if (ChannelList.isEnabled()) {
					List<Channel> TargetChannels = new ArrayList<Channel>();
					for (Object NameObj : ChannelList.getSelectedObjects()) {
						String Name = (String) NameObj;
						Channel Channel = null;
						try {
							Channel = Main.Channels.get(Name);
						} catch (NullPointerException e) {
							e.printStackTrace();
							return;
						}
						TargetChannels.add(Channel);
					}
					
					for (Channel Channel : TargetChannels) {
						for (Client Client : Channel.Members) {
							for (String Line : Lines) {
								Client.SendLine(":" + Main.ServerName + " PRIVMSG " + Channel.Name + " :" + Line);
							}
						}
					}
				} else if (ClientList.isEnabled()) {
					List<Client> TargetClients = new ArrayList<Client>();
					for (Object NameObj : ClientList.getSelectedObjects()) {
						String Name = (String) NameObj;
						for (Client Client : Main.Clients) {
							if (!TargetClients.contains(Client) && Client.Nickname.equals((String) Name)) {
								TargetClients.add(Client);
							}
						}
					}
					
					for (Client Client : TargetClients) {
						for (String Line : Lines) {
							Client.SendLine(":" + Main.ServerName + " PRIVMSG " + Client.Nickname + " :" + Line);
						}
					}
				}
			}
		});
	}
}