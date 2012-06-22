package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;

public class TTFTable_hhea extends TTFTable {
	
	public int version = 0x0001000;		//Fixed version  	0x00010000 (1.0)
	public int ascent = 0;				//FWord	Distance from baseline of highest ascender
	public int descent = 0;				//FWord	Distance from baseline of lowest descender
	public int lineGap = 0;				//FWord	typographic line gap
	public int advanceWidthMax = 0;		//UFWord must be consistent with horizontal metrics
	public int minLeftSideBearing = 0;	//FWord must be consistent with horizontal metrics
	public int minRightSideBearing = 0;	//FWord must be consistent with horizontal metrics
	public int xMaxExtent = 0;			//FWord	max(lsb + (xMax-xMin))
	public int caretSlopeRise = 0;		//short	used to calculate the slope of the caret (rise/run) set to 1 for vertical caret
	public int caretSlopeRun = 0;		//short	0 for vertical
	public int caretOffset = 0;			//FWord	set value to 0 for non-slanted fonts
	public int reserved1 = 0;			//short	set value to 0
	public int reserved2 = 0;			//short set value to 0
	public int reserved3 = 0;			//short set value to 0
	public int reserved4 = 0;			//short set value to 0
	public int metricDataFormat = 0;	//short 0 for current format
	public int numOfLongHorMetrics = 0;	//ushort number of advance widths in metrics table*/
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.version = ttf.readInt();
		this.ascent = ttf.readShort();
		this.descent = ttf.readShort();
		this.lineGap = ttf.readShort();
		this.advanceWidthMax = ttf.readUnsignedShort();
		this.minLeftSideBearing = ttf.readShort();
		this.minRightSideBearing = ttf.readShort();
		this.xMaxExtent = ttf.readShort();
		this.caretSlopeRise = ttf.readShort();
		this.caretSlopeRun = ttf.readShort();
		this.caretOffset = ttf.readShort();
		this.reserved1 = ttf.readShort();
		this.reserved2 = ttf.readShort();
		this.reserved3 = ttf.readShort();
		this.reserved4 = ttf.readShort();		
		this.metricDataFormat = ttf.readShort();
		this.numOfLongHorMetrics = ttf.readUnsignedShort();
		return true;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeInt(this.version);
		ttf.writeShort(this.ascent);
		ttf.writeShort(this.descent);
		ttf.writeShort(this.lineGap);
		ttf.writeShort(this.advanceWidthMax);
		ttf.writeShort(this.minLeftSideBearing);
		ttf.writeShort(this.minRightSideBearing);
		ttf.writeShort(this.xMaxExtent);
		ttf.writeShort(this.caretSlopeRise);
		ttf.writeShort(this.caretSlopeRun);
		ttf.writeShort(this.caretOffset);
		ttf.writeShort(0);
		ttf.writeShort(0);
		ttf.writeShort(0);
		ttf.writeShort(0);
		ttf.writeShort(this.metricDataFormat);
		ttf.writeShort(this.numOfLongHorMetrics);
		this.finishWrite(ttf);
		return true;
	}	
}
