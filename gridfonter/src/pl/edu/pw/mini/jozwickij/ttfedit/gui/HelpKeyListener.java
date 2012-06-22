package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HelpKeyListener implements KeyListener {

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F1)
			HelpWindow.showHelp();			
	}
	public void keyReleased(KeyEvent e) {}		
}