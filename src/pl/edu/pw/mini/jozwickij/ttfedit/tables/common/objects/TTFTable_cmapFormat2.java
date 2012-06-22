package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.Fields;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.TField;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject.Type;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_cmapFormat2 extends TTFTable_cmapFormat {
	
	public final static class Format2SubHeader {
		public int firstCode;
		public int entryCount;
		public int idDelta;
		public int idRangeOffset;
		
		@Fields(
				{@TField(name="firstCode",type=Type.USHORT),
				@TField(name="entryCount",type=Type.USHORT),
				@TField(name="idDelta",type=Type.SHORT),
				@TField(name="idRangeOffset",type=Type.USHORT)}				
		)
		
		public Format2SubHeader(RandomAccessFile ttf) throws Exception {
			this.firstCode = ttf.readUnsignedShort();
			this.entryCount = ttf.readUnsignedShort();
			this.idDelta = ttf.readShort();
			this.idRangeOffset = ttf.readUnsignedShort();			
		}
	}
	
	public int length = 0;
	public int language = 0;
	public int[] subHeaderKeys = new int[256];
	Format2SubHeader subHeaders[] = null;
	int[] glyphIndexArray = null;
			
	public TTFTable_cmapFormat2(RandomAccessFile ttf, int off, int lengthBody, Map<String, TTFTable> tables) throws Exception {
		super(ttf,off,lengthBody, tables);
		this.format = ttf.readUnsignedShort();
		this.length = ttf.readUnsignedShort();
		this.language = ttf.readUnsignedShort();
		
		HashMap<Integer, Integer> m = new HashMap<Integer,Integer>();
		for (int i=0; i<256; i++) {
			subHeaderKeys[i] = ttf.readUnsignedShort();
			m.put(subHeaderKeys[i],0);
		}
		this.subHeaders = new Format2SubHeader[m.size()];
		int glStart = Integer.MAX_VALUE, glEnd = 0;
		long fp = ttf.getFilePointer();
		
		for (int i=0; i<subHeaders.length; i++) {
			subHeaders[i] = new Format2SubHeader(ttf);
			if (subHeaders[i].idRangeOffset + i*8 > glEnd)
				glEnd = subHeaders[i].idRangeOffset + i*8;
			if (subHeaders[i].idRangeOffset + i*8 < glStart)
				glStart = subHeaders[i].idRangeOffset + i*8;
		}
		
		ttf.seek(fp+glStart+8);
		this.glyphIndexArray = new int[glEnd-glStart+1];
		Debug.printlnErr("Format 2 glyph count="+this.glyphIndexArray.length+", glStart at="+glStart+", glEnd at="+glEnd,this);
		for (int i=glStart, j=0; i<glEnd; i++) {
			this.glyphIndexArray[j++] = ttf.readUnsignedShort();
		}
		this.ttfTables = tables;
	}
	
	@Override
	public int getGlyphIdForChar(int ch) {
		int hsb = ch >> 8;
		int lsb = ch & 0xff;
		
		int idx = (hsb != 0) ? this.subHeaderKeys[hsb] / 8 : 0;
	    Format2SubHeader sub = this.subHeaders[idx];
	    if (lsb < sub.firstCode || lsb >= (sub.firstCode + sub.entryCount)) {
	    	return 0;
	    }
	    
	    idx = this.glyphIndexArray[ sub.firstCode + lsb ];
	    if (idx != 0) {
	    	idx += sub.idDelta;
	    	idx %= 65536;
	    }
	    return idx;		
	}

	@Override
	public String getInfo() {
		return "Macintosh CMAP format 2";
	}	
}