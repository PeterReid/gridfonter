package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpWindow extends JFrame implements HyperlinkListener {
	
	private static final long serialVersionUID = 1L;
	private static HelpWindow instance = null;
	private JEditorPane jEditorPane = null;
	
	synchronized private static HelpWindow getInstance() {
		if (instance==null)
			instance = new HelpWindow();
		return instance;
	}
	
	public HelpWindow() {
		this.initialize();
		File f = null;
		try {
			f = new File("./res/help.html");
			this.jEditorPane.setPage(f.toURL());	
		}
		catch (Exception e) {
			this.jEditorPane.setText("Problem while loading help file");			
		}		
	}
	
	public static void showHelp() {
		getInstance().setVisible(true);		
	}
	
	/**
	 * This method initializes jEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			this.jEditorPane.setContentType("text/html");
			jEditorPane.setEditable(false);
			jEditorPane.addHyperlinkListener(this);
		}
		return jEditorPane;
	}
	
	private void initialize() {
		this.setSize(new java.awt.Dimension(600,400));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(getJEditorPane()), java.awt.BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Help");		
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if ( e.getEventType ( ) == HyperlinkEvent.EventType.ACTIVATED ) {
			try {
				jEditorPane.setPage ( e.getURL() ) ;
			}
			catch (IOException ex) {
				jEditorPane.setText("Error loading help");
			}
		}
	}
	
	public static boolean isFocusedWindow() {
		return getInstance().jEditorPane.isFocusOwner();
	}
}
