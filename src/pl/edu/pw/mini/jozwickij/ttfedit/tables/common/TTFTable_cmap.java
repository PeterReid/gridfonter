package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.RandomAccessFile;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComponent;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_cmapSub;

public class TTFTable_cmap extends TTFTable {
	
	public int version = 0;
	public int numberSubtables = 0;	
	public TTFTable_cmapSub[] subtables = null;
	private Box box = null;
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String,TTFTable> tables) throws Exception {
		this.version = ttf.readUnsignedShort();
		this.numberSubtables = ttf.readUnsignedShort();
		this.subtables = new TTFTable_cmapSub[numberSubtables];
		for (int i=0; i<numberSubtables; i++) {
			subtables[i] = new TTFTable_cmapSub(ttf, offset);
		}
		for (int i=0; i<numberSubtables; i++) {
			long len = (i<numberSubtables-1) ?	subtables[i+1].offset - subtables[i].offset : 
												length - subtables[i].offset;
			subtables[i].readFrom(ttf, offset, (int)len, 0, tables);
		}
		return true;
	}

	@Override
	public JComponent getView(Component c, Font f) {
		this.setupView();
		if (box==null) {
			box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(DefaultProperties.SMALL_PAD));
			for (int i=0; i<numberSubtables; i++) {
				Component cmp = subtables[i].getView(c, f);
				if (cmp!=null) {
					box.add(cmp);
				}
			}
			box.add(Box.createVerticalStrut(DefaultProperties.SMALL_PAD));
			view.setLayout(new FlowLayout(FlowLayout.LEADING));
			view.add(Box.createHorizontalStrut(DefaultProperties.SMALL_PAD));
			view.add(box);			
		}
		return spanel;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeShort(this.version);
		ttf.writeShort(this.numberSubtables);
		int cmapLen = 0;
		
		for (int i=0; i < this.numberSubtables; i++) {
			subtables[i].headerStart = this.my_offset;
			subtables[i].offset = 4 + this.numberSubtables*8 + cmapLen;
			subtables[i].writeToFile(ttf,tables);
			cmapLen += subtables[i].offset;			
		}
		ttf.seek(my_offset + 4 + this.numberSubtables*8 + cmapLen);
		this.finishWrite(ttf);
		return true;
	}

	@Override
	public boolean isViewUserFriendly() {
		return true;
	}
}
