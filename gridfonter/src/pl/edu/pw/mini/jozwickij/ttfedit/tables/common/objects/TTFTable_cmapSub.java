package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Component;
import java.awt.Font;
import java.io.RandomAccessFile;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_cmapSub extends TTFTable {
	public int platformID = 0;
	public int platformSpecificID = 3;
	public long offset = 0;
	public int format = 0;
	public int headerStart = 0;
	public TTFTable_cmapFormat cmapFormat = null;
	private final static long UINT_MASK = (1L << 32)-1L;
			
	@Fields(
			{@TField(name="platformID",type=Type.USHORT),
			@TField(name="platformSpecificID",type=Type.USHORT),
			@TField(name="offset",type=Type.INT),
			@TField(name="format",type=Type.USHORT),
			@TField(name="cmapFormat",type=Type.OBJ)}
	)
	
	public TTFTable_cmapSub(RandomAccessFile ttf, int cmapStart) throws Exception {
		this.platformID = ttf.readUnsignedShort();
		this.platformSpecificID = ttf.readUnsignedShort();
		this.offset = ttf.readInt() & UINT_MASK;
		long fp = ttf.getFilePointer();
		
		ttf.seek(cmapStart+offset);
		long fstart = ttf.getFilePointer();
		this.format = ttf.readUnsignedShort();
		ttf.seek(fstart);
		int format32 = ttf.readInt();
		ttf.seek(fstart);
		int format8p = ((format32 & 0xffff0000) >> 16);
		Debug.println("CMAP Format "+format+" present (platform="+platformID+",specificID="+platformSpecificID+")",this);
		if (format8p>6 && format8p<12) {
			Debug.printlnErr("32-bit CMAP format v"+format8p+" not supported",this);
		}
		ttf.seek(fp);		
	}
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int cmapOffset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		switch (format) {
		case 0:
			cmapFormat = new TTFTable_cmapFormat0(ttf,(int) (cmapOffset+offset),length,tables);
			break;
		case 2:
			cmapFormat = new TTFTable_cmapFormat2(ttf,(int) (cmapOffset+offset),length,tables);
			break;
		case 4:
			cmapFormat = new TTFTable_cmapFormat4(ttf,(int) (cmapOffset+offset),length,tables);
			break;
		case 6:
			cmapFormat = new TTFTable_cmapFormat6(ttf,(int) (cmapOffset+offset),length,tables);
			break;
		case 12:
			cmapFormat = new TTFTable_cmapFormat12(ttf,(int) (cmapOffset+offset),length,tables);
			break;
		default:
			cmapFormat = new TTFTable_cmapFormat(ttf,(int) (cmapOffset+offset),length, tables);
		}
		return true;
	}

	@Override
	public JComponent getView(Component c, Font f) {
		String header = TTFTable_nameRecord.getRecordDesc(this.platformID, this.platformSpecificID);
		
		Component cmp = cmapFormat.getView(header,f);
		if (cmp!=null) {
			this.setupView();
			view.setLayout(new BoxLayout(view, BoxLayout.LINE_AXIS));
			view.add(cmp);
			view.add(Box.createHorizontalGlue());
			return view;
		}
		return null;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		ttf.writeShort(this.platformID);
		ttf.writeShort(this.platformSpecificID);
		ttf.writeInt((int)offset);
		long fp = ttf.getFilePointer();
		this.offset = cmapFormat.write(ttf, (int) (headerStart + offset));
		ttf.seek(fp);
		return true;
	}
}
