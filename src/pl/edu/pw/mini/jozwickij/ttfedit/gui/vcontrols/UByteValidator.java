package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;

public class UByteValidator extends Validator {
	
	private static Validator _this = null;

	public Object validate(Object o) throws Exception {
		if (o instanceof String) {
			Integer i = null;
			try {
				i = Integer.parseInt((String)o);
			}
			catch (Exception e) {
				throw new InfoException("Value should be in range 0 to 255, but is "+i);
			}
			if (i > 255 || i < 0)
				throw new InfoException("Value should be in range 0 to 255, but is "+i);
			return i;		
		}
		else if (o instanceof Integer) {
			Integer i = (Integer) o;
			if (i > 255 || i < 0)
				throw new InfoException("Value should be in range 0 to 255, but is "+i);
			return i;
		}
		throw new InfoException("Invalid object value");		
	}
	
	synchronized public static Validator getInstance() {
		if (_this == null) {
			_this = new UByteValidator();
		}
		return _this;
	}	

}
