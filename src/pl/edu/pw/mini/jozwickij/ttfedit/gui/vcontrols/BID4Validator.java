package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;

public class BID4Validator extends Validator {
	
	private static Validator _this = null;

	public Object validate(Object o) throws Exception {
		if (o instanceof String) {
			try {
				String s = (String)o;
				if (s.length() > 4) {
					s = s.substring(0,4);
				}
				for (int i=s.length(); i<4; i++) {
					s+=" ";
				}
				return s.getBytes("iso-8859-1");
			}
			catch (Exception e) {
				throw new InfoException("String cannot be represented as Latin-2 bytes");
			}
		}
		else if (o.getClass().equals(byte[].class)) {
			try {
				byte[] b = (byte[]) o;
				String s = new String(b);
				if (s.length() > 4) {
					s = s.substring(0,4);
				}
				for (int i=s.length(); i<4; i++) {
					s+=" ";
				}
				return s.getBytes("iso-8859-1");
			}
			catch (Exception e) {
				throw new InfoException("String cannot be represented as Latin-2 bytes");
			}
		}
		throw new Exception("Invalid object value");		
	}
	
	synchronized public static Validator getInstance() {
		if (_this == null) {
			_this = new BID4Validator();
		}
		return _this;
	}	

}
