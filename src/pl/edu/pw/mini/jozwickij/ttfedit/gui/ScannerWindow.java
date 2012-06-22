package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.tools.Scanner;
import pl.edu.pw.mini.jozwickij.ttfedit.tools.ScannerCallback;

public class ScannerWindow extends JFrame implements ScannerCallback {
	
	private static final long serialVersionUID = 1L;
	private static ScannerWindow instance = null;
	private JEditorPane jEditorPane = null;
	private JEditorPane jResultPane = null;
	private String results = "<html><body style='padding-left: 10px'>";
	private String header1 = "<html><body style='padding-left: 10px' bgcolor='navy' text='white'><h2>";
	private String header2 = "</h2></body></html>";
		
	synchronized private static ScannerWindow getInstance() {
		if (instance==null)
			instance = new ScannerWindow();
		return instance;
	}
	
	public ScannerWindow() {
		this.initialize();
		this.jEditorPane.setText(header1+header2);
		this.jResultPane.setText(results);
		this.jEditorPane.setDoubleBuffered(true);
		this.jResultPane.setDoubleBuffered(true);
		this.jEditorPane.setBorder(new EmptyBorder(0,0,0,0));
		this.jResultPane.setBorder(new EmptyBorder(0,0,0,0));
		this.jEditorPane.setPreferredSize(new java.awt.Dimension(600,50));
	}
	
	public static void showWindow(String path) {
		getInstance().setVisible(true);
		Scanner.scan(path, getInstance());
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
		}
		return jEditorPane;
	}
	
	/**
	 * This method initializes jResultPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getJResultPane() {
		if (jResultPane == null) {
			jResultPane = new JEditorPane();
			this.jResultPane.setContentType("text/html");
			jResultPane.setEditable(false);			
		}
		return jResultPane;
	}
	
	private void initialize() {
		this.setSize(new java.awt.Dimension(600,400));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(getJEditorPane()), java.awt.BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(getJResultPane()), java.awt.BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);		
		this.setTitle("Testing fonts in directory");
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.LINE_AXIS));
		bar.add(Box.createHorizontalGlue());
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Scanner.cancel();
			}			
		});
		bar.add(stop);
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!Scanner.isRunning())
					setVisible(false);
			}			
		});
		bar.add(Box.createHorizontalStrut(5));
		bar.add(close);
		bar.add(Box.createHorizontalStrut(15));
		bar.setBorder(new EmptyBorder(5,5,5,5));
		this.add(bar, BorderLayout.SOUTH);
	}

	public static boolean isFocusedWindow() {
		return getInstance().jEditorPane.isFocusOwner();
	}

	public void onScan(final String filename, final boolean start) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (start)
					jEditorPane.setText(header1+"Checking file "+filename+"...<br>"+header2);
				else
					jResultPane.setText(results+="<p style='color: green'>File "+filename+" is OK</p>");
				
			}
		});		
	}

	public void onException(final String filename, final int mode, final String info) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jResultPane.setText(results+="<p style='color: red'>File "+filename+
						" has errors ("+(mode==0 ? "LOAD" : "SAVE")+"):"+info+" </p>");
			}
		});		
	}
	
	public void onEnd(final boolean aborted) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jEditorPane.setText(header1+(aborted ? "Aborted" : "Completed")+header2);
			}
		});		
	}

	@Override
	public void setVisible(boolean b) {
		if (b || (!b && !Scanner.isRunning()))
			super.setVisible(b);
	}	
}
