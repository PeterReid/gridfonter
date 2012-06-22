package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;

public class Validator {
	
	public Object validate(Object o) throws Exception {
		throw new InfoException("No validator available!");
	}
	public static Validator getInstance() throws Exception {
		throw new InfoException("No validator available!");
	}		
}