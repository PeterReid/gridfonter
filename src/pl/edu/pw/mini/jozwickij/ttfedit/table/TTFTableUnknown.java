package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;

public class TTFTableUnknown extends TTFTable {
	private String tag = "";
	private JLabel label = null;
	
	public TTFTableUnknown(String tagname) {
		this.tag = tagname;
	}
	
	@Override
	public String getTableName() {
		return tag;
	}

	@Override
	public JComponent getView(Component c, Font f) {
		if (DefaultProperties.SHOW_TABLES) {
			this.setupView();
			
			if (label==null) {
				label = new JLabel(DefaultProperties.NO_TABLE_TEMPLATE+". "+
								   "Length of this table is "+
								   (this.buffer.length-TTFTables.PADDING)+
								   " bytes.");
				view.add(label,BorderLayout.NORTH);
			}
			return spanel;
		}
		else {
			return null;
		}
	}	
}
