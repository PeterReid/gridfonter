package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import javax.swing.UIManager;

import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class JGUIFrame extends GUIFrame {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize() {
		try {
					
		}
		catch (Exception e) {
			Debug.printlnErr("Error while initializing look and feel: "+e,this);
		}		
	}
	
	public static void main(String[] args)
	{
		parseArgs(args);
		JGUIFrame gui = null;
		try {
			gui = new JGUIFrame();		
		}
		catch(Exception e) {
			Util.showException(e, gui, "Exception");
		}
	}
}
