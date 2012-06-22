package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;

public class TTFOffsetSubtable extends TTFTable {
	
	public final static int FIELD1_VALUE_APPLE = 0x74727565;
	public final static int FIELD1_VALUE_MSWIN = 0x00010000;
	
	public int scalerType = 0;
	public int numTables = 0;
	public int searchRange = 0;
	public int entrySelector = 0;
	public int rangeShift = 0;
    
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String,TTFTable> tables) throws Exception {
		ttf.seek(0);
		scalerType = ttf.readInt();
    	numTables = ttf.readUnsignedShort();
    	searchRange = ttf.readUnsignedShort();
        entrySelector = ttf.readUnsignedShort();
        rangeShift = ttf.readUnsignedShort();
        return true;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String,TTFTable> tables) throws Exception
    {
    	int cnt = 0;
		for (TTFTable tab : tables.values()) {
    		if (TTFont.isTableOK(tab.getTableName())) {
    			cnt++;
    		}
    	}
		
		this.entrySelector = (int)(Math.log10(cnt)/Math.log10(2));
		this.searchRange = (int)Math.pow(2, entrySelector);
		
		ttf.seek(0);
    	ttf.writeInt(scalerType);
    	ttf.writeShort(this.numTables=cnt);
    	ttf.writeShort(searchRange*16);
        ttf.writeShort(entrySelector);
        ttf.writeShort(rangeShift=(cnt-searchRange)*16);
        ttf.seek(12+cnt*16);
        return true;
    }

	@Override
	public String getTableName() {
		return "@offsets";
	}
}
