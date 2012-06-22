package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import javax.swing.UIManager;

import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

public class JGUIFrame extends GUIFrame {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize() {
		try {
			if (!Util.isMac()) {
				PlasticLookAndFeel.set3DEnabled(true);
			    Options.setPopupDropShadowEnabled(true);
				Options.setUseNarrowButtons(true);
				Options.setUseSystemFonts(true);
				UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
			}			
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
