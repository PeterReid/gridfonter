package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;

public class BStringValidator extends Validator {
	
	private static Validator _this = null;

	public Object validate(Object o) throws Exception {
		if (o instanceof String) {
			try {
				return ((String)o).getBytes("iso-8859-1");
			}
			catch (Exception e) {
				throw new InfoException("String cannot be represented as Latin-2 bytes");
			}
		}
		else if (o.getClass().equals(byte[].class)) {
			try {
				byte[] b = (byte[]) o;
				String s = new String(b);
				return s.getBytes("iso-8859-1");
			}
			catch (Exception e) {
				throw new InfoException("String cannot be represented as Latin-2 bytes");
			}
		}
		throw new InfoException("Invalid object value");		
	}
	
	synchronized public static Validator getInstance() {
		if (_this == null) {
			_this = new BStringValidator();
		}
		return _this;
	}	

}
