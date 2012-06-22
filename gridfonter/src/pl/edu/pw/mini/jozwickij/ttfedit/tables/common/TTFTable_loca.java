package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;

public class TTFTable_loca extends TTFTable {
	
	public int[] offsets = null;
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		TTFTable maxp = tables.get(TTFTables.MAXP);
		TTFTable head = tables.get(TTFTables.HEAD);
		if (maxp==null || head==null) {
			return false; /* we need to be processed later */
		}
		int numGlyphs = ((TTFTable_maxp)maxp).numGlyphs;
		int offsetKind= ((TTFTable_head)head).indexToLocFormat;
		offsets = new int[numGlyphs+1];
		if (offsetKind == 0) { /* short offset */
			for (int i=0; i<offsets.length; i++) {
				offsets[i] = ttf.readUnsignedShort()*2;				
			}
		}
		else if (offsetKind == 1) {  /* long offset */
			for (int i=0; i<offsets.length; i++) {
				offsets[i] = ttf.readInt();				
			}
		}
		return true;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		TTFTable head = tables.get(TTFTables.HEAD);
		int offsetKind= ((TTFTable_head)head).indexToLocFormat;
		if (offsetKind == 0) {
			for (int i=0; i<offsets.length; i++) {
				ttf.writeShort((offsets[i]/2) & Short.MAX_VALUE);				
			}
		}
		else if (offsetKind == 1) {
			for (int i=0; i<offsets.length; i++) {
				ttf.writeInt(offsets[i]);
			}
		}
		this.finishWrite(ttf);
		return true;
	}	
}
