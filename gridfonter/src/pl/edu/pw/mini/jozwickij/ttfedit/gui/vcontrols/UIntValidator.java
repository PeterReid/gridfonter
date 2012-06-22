package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;

public class UIntValidator extends Validator {
	
	private static Validator _this = null;
	private static long MAX_UINT = (1L << 32)-1;

	public Object validate(Object o) throws Exception {
		if (o instanceof String) {
			Long l = null;
			try {
				l = Long.parseLong((String)o);
			}
			catch (Exception e) {
				throw new InfoException("Value should be in range 0 to "+MAX_UINT+" , but is "+l);
			}
			if (l > MAX_UINT || l < 0L)
				throw new InfoException("Value should be in range 0 to "+MAX_UINT+" , but is "+l);
			return l.intValue();
		}
		else if (o instanceof Long) {
			Long l = (Long) o;
			if (l > MAX_UINT || l < 0L)
				throw new InfoException("Value should be in range 0 to "+MAX_UINT+" , but is "+l);
			return l.intValue();
		}
		throw new InfoException("Invalid object value");		
	}
	
	synchronized public static Validator getInstance() {
		if (_this == null) {
			_this = new UIntValidator();
		}
		return _this;
	}	

}
