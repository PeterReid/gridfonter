import pl.edu.pw.mini.jozwickij.ttfedit.gui.GUIFrame;
import pl.edu.pw.mini.jozwickij.ttfedit.gui.JGUIFrame;

public class TTFEdit {
	
	public static void main(String[] args)
	{
		/* Sometimes Plastic may not work, so do not use it */
		try {
			JGUIFrame.main(args);
		}
		catch (Error er) {
			GUIFrame.main(args);
		}
	}
}
 