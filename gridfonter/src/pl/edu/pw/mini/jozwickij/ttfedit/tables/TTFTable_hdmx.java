package pl.edu.pw.mini.jozwickij.ttfedit.tables;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_maxp;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_hdmx extends TTFTable {
	
	public final static int VER_0 = 0;
	
	final static class DeviceRecord0 {
		public byte pixelSize = 0;		//pixel size for following widths
		public byte maximumWidth = 0;	//
		public byte[] widths = null;	// widths[number of glyphs]
		
		public DeviceRecord0(RandomAccessFile ttf, int numGlyphs) throws Exception {
			this.pixelSize = ttf.readByte();
			this.maximumWidth = ttf.readByte();
			this.widths = new byte[numGlyphs];
			ttf.readFully(this.widths);
		}

		public void write(RandomAccessFile ttf) throws IOException {
			ttf.writeByte(pixelSize);
			ttf.writeByte(maximumWidth);
			ttf.write(widths);			
		}
	}
	
	public int format = 0;			//version number
	public int numberOfRecords = 0;	//number of device records
	public int size = 0;			//size of a device record, long aligned
	DeviceRecord0[] records = null;	//records[number of device records]
	
	public @Override boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		TTFTable maxp = tables.get(TTFTables.MAXP);
		if (maxp==null) {
			return false; /* we need to be processed later */
		}
		int numGlyphs = ((TTFTable_maxp)maxp).numGlyphs;
		
		this.format = ttf.readShort();
		this.numberOfRecords = ttf.readShort();
		this.size = ttf.readInt();
		Debug.println("hdmx is version "+format,this);
		if (format == VER_0) {
			long fp = ttf.getFilePointer();
			this.records = new DeviceRecord0[numberOfRecords];
			for (int i=0; i<numberOfRecords; i++) {
				ttf.seek(fp+size*i);
				this.records[i] = new DeviceRecord0(ttf,numGlyphs);
			}
		}
		return true;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		if (format!=VER_0)
			return super.writeToFile(ttf, tables);
		TTFTable_glyf glyfTab = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		int numGlyphs = glyfTab.glyphs.size();		
		ttf.writeShort(format=VER_0);
		ttf.writeShort(records.length);
		ttf.writeInt(2+numGlyphs);
		for (int i=0; i < records.length; i++) {
			records[i].write(ttf);
		}
		this.finishWrite(ttf);
		return true;
	}
}
