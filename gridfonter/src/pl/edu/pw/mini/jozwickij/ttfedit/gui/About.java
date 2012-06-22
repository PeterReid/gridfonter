package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.Resources;

public class About extends JFrame {

	private static final long serialVersionUID = 1L;
	private static About instance = null;
	
	synchronized public static void showAbout() {
		if (instance==null)
			instance = new About();
		instance.setVisible(true);	
	}
	
	public About() {
		this.setSize(350,220);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setTitle("About");
		URL imgURL = Resources.get(About.class,"./res/font.png");
		this.setLayout(new BorderLayout());
		this.getRootPane().setBorder(new EmptyBorder(20,20,20,20));
		try {
			ImageIcon icon = new ImageIcon(imgURL);
			this.add(new JLabel(icon), BorderLayout.WEST);			
		}
		catch (Exception e) {
			this.add(Box.createHorizontalStrut(128), BorderLayout.WEST);
		}
		this.add(new JLabel("<html>TTFEdit - TrueType tables editor<br>"+DefaultProperties.PVER + "<br><br>"+
							 "(C) 2006 Jakub Jóźwicki, MiNI PW<br></html>"), BorderLayout.CENTER);
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.LINE_AXIS));
		bar.add(Box.createHorizontalGlue());
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
			}			
		});
		bar.add(ok);
		this.add(bar, BorderLayout.SOUTH);
		this.setLocationRelativeTo(null);
	}

}
