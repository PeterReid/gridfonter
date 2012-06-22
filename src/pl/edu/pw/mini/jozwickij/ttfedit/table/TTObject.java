package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.awt.Component;
import java.awt.Font;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import javax.swing.JComponent;

import pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols.Validator;

public class TTObject {
	
	/* annotations */
	public enum Type { BYTE, UBYTE, SHORT, USHORT, INT, FIXED, LONG, ULONG, STRING, OBJ };
	
	@Retention(RetentionPolicy.RUNTIME) @Inherited
	public @interface TField {
		String name();
		Type type();
		boolean rw() default true;
		String enumPrefix() default "";
		Class validator() default Validator.class;
	}	
	@Inherited @Retention(RetentionPolicy.RUNTIME)
	public @interface Fields {
		TField[] value() default {};
	}
	
	protected TField[] getFields() {
		Fields fields = null;
		for (Method mth : this.getClass().getMethods()) {
			fields = mth.getAnnotation(Fields.class);
			if (fields!=null) {
				TField[] tfields = fields.value();
				if (tfields!=null) {
					return tfields;
				}					
			}
        }
		return new TField[] {};
	}
	
	public JComponent getView(Component c, Font f) { return null; }
}
