package JIRCd.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NoChannelsSelectedError extends JDialog {
	private static final long serialVersionUID = 1L;
	public NoChannelsSelectedError(JFrame parent) {
		super(parent, "No clients selected", true);
		Box b = Box.createVerticalBox();
		b.add(Box.createGlue());
		b.add(new JLabel("You must select a channel."));
		b.add(Box.createGlue());
		getContentPane().add(b, "Center");
		JPanel p2 = new JPanel();
		JButton ok = new JButton("Ok");
		p2.add(ok);
		getContentPane().add(p2, "South");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});
		setSize(250, 150);
		setLocation((parent.getLocation().x + (parent.getWidth() / 2)) - 125, (parent.getLocation().y + (parent.getHeight() / 2)) - 75);
		setVisible(true);
	}
}