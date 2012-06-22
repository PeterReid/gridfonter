package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.Fields;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.TField;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.Type;

public class TTFTable_cmapFormat0 extends TTFTable_cmapFormat {
	public int length = 262;
	public int language = 0; /* language-independent (any 8-bit encodings can be mapped)*/
	int[] glyphIndexArray = new int[256];
	
	@Fields(
			{@TField(name="format",type=Type.USHORT),
			@TField(name="length",type=Type.USHORT),
			@TField(name="language",type=Type.SHORT),
			@TField(name="glyphIndexArray",type=Type.OBJ)}				
	)
	
	public TTFTable_cmapFormat0(RandomAccessFile ttf, int off, int lengthBody, Map<String, TTFTable> tables) throws Exception {
		super(ttf,off,lengthBody, tables);
		ttf.seek(off);
		this.format = ttf.readUnsignedShort();
		this.length = ttf.readUnsignedShort();
		this.language = ttf.readUnsignedShort();
		for (int i=0; i<this.glyphIndexArray.length; i++) {
			this.glyphIndexArray[i] = ttf.readUnsignedByte();
		}
		this.ttfTables = tables;
	}
	
	@Override
	public int getGlyphIdForChar(int ch) {
		return (ch<0 || ch>255) ? 0 : this.glyphIndexArray[ch];			
	}

	@Override
	public String getInfo() {
		return "Macintosh CMAP format 0";
	}

	@Override
	public int write(RandomAccessFile ttf, int off) throws IOException {
		this.prepareWrite(ttf, off);
		ttf.writeShort(this.format);
		ttf.writeShort(this.length = 262);
		ttf.writeShort(this.language);
		for (int i=0; i< Math.min(glyphIndexArray.length,256); i++) {
			ttf.writeByte(this.glyphIndexArray[i]);
		}
		for (int i=Math.max(glyphIndexArray.length,256); i<256; i++) {
			ttf.writeByte(0);
		}
		return this.finishWrite(ttf,off);
	}
	
	
}
