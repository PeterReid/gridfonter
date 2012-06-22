package pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class VControl extends JPanel implements FocusListener {

	public final static int PAD = DefaultProperties.PAD;
	public final static int LBREAK = DefaultProperties.LBREAK;
	public final static Validator SHORT 	= ShortValidator.getInstance();
	public final static Validator USHORT 	= UShortValidator.getInstance();
	public final static Validator UINT	 	= UIntValidator.getInstance();
	public final static Validator UBYTE	 	= UByteValidator.getInstance();
	public final static Validator BSTRING	= BStringValidator.getInstance();
	public final static Validator BID4		= BID4Validator.getInstance();
	private static final long serialVersionUID = 1L;
	private JLabel label = new JLabel();
	private JTextField fieldValueView = new JTextField();
	private Field field = null;
	private Object object = null;
	private Validator vdtor = null;
	private Object initialVal = null;
	private String fieldName = null;
	
	public VControl(String name, String desc, Object owner, Validator validator) {
		this.object = owner;
		this.vdtor = validator;
		
		label.setText(desc);
		this.fieldName = name;
		this.setVisibleValue();
		if (vdtor instanceof UIntValidator)
			fieldValueView.setPreferredSize(new Dimension(4*PAD,PAD));
		else
			fieldValueView.setPreferredSize(new Dimension(3*PAD,PAD));
		
		fieldValueView.setMaximumSize(new Dimension(4*PAD,PAD));
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.label.setAlignmentX(Box.LEFT_ALIGNMENT);
		this.add(this.label);
		this.add(Box.createHorizontalStrut(PAD/2));
		this.add(Box.createHorizontalGlue());
		this.fieldValueView.setAlignmentX(Box.RIGHT_ALIGNMENT);
		this.add(this.fieldValueView);
		ToolTipManager tpManager = ToolTipManager.sharedInstance();
		tpManager.setDismissDelay(10000);
	}
	
	private void setVisibleValue() {
		try {
			field = object.getClass().getDeclaredField(fieldName);
			initialVal = field.get(object);
			fieldValueView.setText(objToString(initialVal));
			if (vdtor != null) {
				fieldValueView.addFocusListener(this);
			}
			else {
				fieldValueView.setEditable(false);
			}
		}
		catch (Exception e) {
			fieldValueView.setText("");
			fieldValueView.setEnabled(false);
		};
	}
	public VControl(String name, String desc, String enumPrefix, Object owner, Validator validator) {
		this(name,desc,owner,validator);
		Field[] fields = object.getClass().getDeclaredFields();
		String tipText = "<html>";
		int lbreak = LBREAK;
		for (Field f : fields) {
			String fname = f.getName();
			if (fname.startsWith(enumPrefix)) {
				try {
					tipText += fname + "=" + f.get(object)+" &nbsp; ";
					if (--lbreak==0) {
						tipText += "<br>";
						lbreak = LBREAK;
					}
				}
				catch (Exception e) {}
			}			
		}
		tipText += "</html>";
		label.setToolTipText(tipText);
		fieldValueView.setToolTipText(tipText);
		this.setToolTipText(tipText);
	}

	private String objToString(Object o) {
		if (o.getClass().equals(byte[].class)) {
			return new String((byte[])o);
		}
		else if (vdtor instanceof UIntValidator) {
			return ((Long)o)+"";
		}
		else
			return o+"";
	}
	
	private void validateObj() {
		try {
			Object setValue = vdtor.validate(fieldValueView.getText());
			field.set(object, setValue);
			initialVal = setValue+"";
		}
		catch (InfoException e) {
			fieldValueView.setText(objToString(initialVal));
			Util.showExceptionInfo(e, null, "Error while setting new value");
		}
		catch (Exception e) {
			fieldValueView.setText(objToString(initialVal));
			Util.showException(e, null, "Error while setting new value");
		}	
	}
	
	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		validateObj();		
	}
	
	public void pinInto(Box box) {
		this.setAlignmentX(Box.LEFT_ALIGNMENT);
		box.add(this);
	}

	public void updateField() {
		this.setVisibleValue();
	}	
}
