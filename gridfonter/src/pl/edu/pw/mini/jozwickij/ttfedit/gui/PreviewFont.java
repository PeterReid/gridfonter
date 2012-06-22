package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_nameRecord;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class PreviewFont extends JFrame implements KeyListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	private final static String FNAME = "./fpreview.ttf";
	private static PreviewFont instance = null;
	private static Thread wThread = null;
	private TTFont ttfont = null;
	private Font jfont = null;
	private JSplitPane jSplitPane = null;
	private JTextPane jTextPane = null;
	private JEditorPane jEditorPane = null;
	private JPanel jfontPanel = null;
	private JSlider jslider = null;	
	
	private void storeFont() {
		FileInputStream is = null;
		try {
			/* Fix for JVM problem: 
			 * fonts are cached by name and changes in glyphs are not visible */
			TTFTable_nameRecord.makeTempNames();
			this.ttfont.save(FNAME);
			is = new FileInputStream(FNAME);
			jfont = Font.createFont( Font.TRUETYPE_FONT, is );
			if (jfont!=null)
				showWindow();	
		}
		catch (Exception e) {						
			Util.showExceptionInfo(e,null,"Error while saving font for preview");			
		}
		finally {
			if (is!=null)
				try {
					is.close();
				}
				catch (Exception ex) {
					Debug.println("Could not close font stream used in preview window",this);
				}
		}
	}
	
	public PreviewFont() {
		this.initialize();
	}
	
	private void initialize() {
		this.setSize(new java.awt.Dimension(600,400));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Text preview");
	}
	
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setDividerLocation(180);
			jSplitPane.setBottomComponent( getJFontPanel() );
			jSplitPane.setTopComponent( new JScrollPane(getJTextPane()) );			
		}
		return jSplitPane;
	}
	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.addKeyListener(this);			
		}
		return jTextPane;
	}
	/**
	 * This method initializes jEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditable(false);			
		}
		return jEditorPane;
	}
		
	/**
	 * This method initializes jfontPanel	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJFontPanel() {
		if (jfontPanel == null) {
			jfontPanel = new JPanel();
			jfontPanel.setLayout(new BorderLayout());
			jfontPanel.add(new JScrollPane(getJEditorPane()), BorderLayout.CENTER);
			jfontPanel.add(getJSlider(), BorderLayout.EAST);
		}
		return jfontPanel;
	}
	
	private JSlider getJSlider() {
		if (jslider==null) {
			jslider = new JSlider(JSlider.VERTICAL, 4, 400, 24);
			jslider.addChangeListener(this);
		}
		return jslider;
	}
	
	synchronized public static void preview(TTFont _font) {
		if (_font==null)
			return;
		
		if (instance==null)
			instance = new PreviewFont();
		instance.ttfont = _font;
		if (wThread==null || !wThread.isAlive()) {
			(wThread = new Thread() {
				public void run() {
					instance.storeFont();
				}
			}).start();
		}
		else {
			Debug.println("Already saving font for preview",PreviewFont.class);
		}
	}

	private void showWindow() {
		this.setVisible(true);
		this.jEditorPane.setFont(jfont.deriveFont(24f));
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		this.jEditorPane.setText(this.jTextPane.getText());
	}

	public void stateChanged(ChangeEvent e) {
		int size = jslider.getValue();
		if (jfont!=null) {
			jEditorPane.setFont(jfont.deriveFont((float)size));
		}
		jslider.setToolTipText(size+" px");
	}
}
