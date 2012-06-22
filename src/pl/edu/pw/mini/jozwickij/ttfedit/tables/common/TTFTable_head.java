package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.Box;
import javax.swing.JComponent;

import pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols.VControl;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFDirEntry;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_head extends TTFTable {
	
	public final static int HEAD_MAGIC = 0x5F0F3CF5;
	
	public final static int FLAG_BASELINE_0 = 1 << 0;// bit 0 - y value of 0 specifies baseline
	public final static int FLAG_LSB_1	    = 1 << 1;// bit 1 - x position of left most black bit is LSB
	public final static int FLAG_SCALEDIFF_2= 1 << 2;// bit 2 - scaled point size and actual point size will differ (i.e. 24 point glyph differs from 12 point glyph scaled by factor of 2)
	public final static int FLAG_SCALEINT_3 = 1 << 3;// bit 3 - use integer scaling instead of fractional
	public final static int FLAG_MSCALER_4  = 1 << 4;// bit 4 - (used by the Microsoft implementation of the TrueType scaler)
	public final static int FLAG_VERTICAL_5 = 1 << 5;// bit 5 - This bit should be set in fonts that are intended to e laid out vertically, and in which the glyphs have been drawn such that an x-coordinate of 0 corresponds to the desired vertical baseline.
	public final static int FLAG_BITZEROED_6= 1 << 6;// bit 6 - This bit must be set to zero.
	public final static int FLAG_ARABIC_7   = 1 << 7;// bit 7 - This bit should be set if the font requires layout for correct linguistic rendering (e.g. Arabic fonts).
	public final static int FLAG_MORPFGX_8  = 1 << 8;// bit 8 - This bit should be set for a GX font which has one or more metamorphosis effects designated as happening by default.
	public final static int FLAG_RTLGLYPH_9 = 1 << 9;// bit 9 - This bit should be set if the font contains any strong right-to-left glyphs.
	public final static int FLAG_INDIC_10   = 1 << 10;//bit 10 - This bit should be set if the font contains Indic-style rearrangement effects.
	public final static int FLAG_ADOBE_11   = 1 << 11;//bits 11-12 - Defined by Adobe.
	public final static int FLAG_ADOBE_12   = 1 << 12;//bits 11-12 - Defined by Adobe.
	
	public final static int STYLE_NONE 			= 0 << 0;	//no style specified
	public final static int STYLE_DEFAULT 		= 1 << 0;	//bit 0 probably default, not in spec
	public final static int STYLE_ITALIC 		= 1 << 1;	//bit 1 italic
	public final static int STYLE_UNDERLINE 	= 1 << 2;	//bit 2 underline
	public final static int STYLE_OUTLINE 		= 1 << 3;	//bit 3 outline
	public final static int STYLE_SHADOW 		= 1 << 4;	//bit 4 shadow
	public final static int STYLE_CONDENSED		= 1 << 5;	//bit 5 condensed (narrow)
	public final static int STYLE_EXTENDED		= 1 << 6;	//bit 6 extended
	
	public final static int DIR_MIXED = 0;		// Mixed directional glyphs
	public final static int DIR_LTR   = 1;		// Only strongly left to right glyphs
	public final static int DIR_LTR_LT= 2;		// Like 1 but also contains neutrals
	public final static int DIR_RTL	  = -1;		// Only strongly right to left glyphs
	public final static int DIR_RTL_LT= -2;		// Like -1 but also contains neutrals
	
	//////////////////////////////////////////////
	public int version = 0x00010000;			// if (version 1.0)
	public int fontRevision = 0x00010000;		// set by font manufacturer
	public int checkSumAdjustment = 0;			// int32 To compute: set it to 0, calculate the checksum for the 'head' table and put it in the table directory, sum the entire font as uint32, then store B1B0AFBA - sum. The checksum for the 'head' table will not be wrong. That is OK.
	public int magicNumber = HEAD_MAGIC;		// set to 0x5F0F3CF5
	public int flags = 0;
		
	public int unitsPerEm = 64;			// 	range from 64 to 16384
	public long created = 0;			//	international date
	public long modified = 0;			//	international date
	public int xMin = 0;				//	for all glyph bounding boxes
	public int yMin = 0;				//	for all glyph bounding boxes
	public int xMax = 0;				//	for all glyph bounding boxes
	public int yMax = 0;				//	for all glyph bounding boxes
	public int macStyle = 0;
		
	public int lowestRecPPEM = 10;		// smallest readable size in pixels
	public int fontDirectionHint = DIR_RTL_LT;	
	public int indexToLocFormat = 0;	// 0 for short offsets, 1 for long
	public int glyphDataFormat = 0;		// 0 for current format
	
	private Box box = null;
	
	@Override 
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.version = ttf.readInt();
		this.fontRevision = ttf.readInt();
		this.checkSumAdjustment = ttf.readInt();
		this.magicNumber = ttf.readInt();
		if (magicNumber != HEAD_MAGIC) {
			Debug.printlnErr("Magic numer error in head table!", this);
		}
		this.flags = ttf.readUnsignedShort();
		this.unitsPerEm = ttf.readUnsignedShort();;
		this.created = ttf.readLong();
		this.modified = ttf.readLong();
		this.xMin = ttf.readUnsignedShort();
		this.yMin = ttf.readUnsignedShort();
		this.xMax = ttf.readUnsignedShort();
		this.yMax = ttf.readUnsignedShort();
		this.macStyle = ttf.readUnsignedShort();
		this.lowestRecPPEM = ttf.readUnsignedShort();
		this.fontDirectionHint = ttf.readUnsignedShort();
		this.indexToLocFormat = ttf.readUnsignedShort();
		this.glyphDataFormat = ttf.readUnsignedShort();
		return true;
	}
	
	private long getTime() throws ParseException {
		GregorianCalendar today = new GregorianCalendar();
		GregorianCalendar itime = new GregorianCalendar(1904, 1, 1, 0, 0, 0);
		itime.setTimeZone(TimeZone.getTimeZone("UTC"));
		return (today.getTimeInMillis() - itime.getTimeInMillis())/1000;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeInt(this.version);
		ttf.writeInt(this.fontRevision);
		ttf.writeInt(this.checkSumAdjustment=0);
		ttf.writeInt(HEAD_MAGIC);
		ttf.writeShort(this.flags);
		ttf.writeShort(this.unitsPerEm);
		ttf.writeLong(this.created);
		ttf.writeLong(getTime());
		ttf.writeShort(this.xMin);
		ttf.writeShort(this.yMin);
		ttf.writeShort(this.xMax);
		ttf.writeShort(this.yMax);
		ttf.writeShort(this.macStyle);
		ttf.writeShort(this.lowestRecPPEM);
		ttf.writeShort(this.fontDirectionHint);
		ttf.writeShort(this.indexToLocFormat);
		ttf.writeShort(this.glyphDataFormat);
		this.finishWrite(ttf);
		return true;
	}

	@Override
	public JComponent getView(Component c, Font f) {
		this.setupView();
		if (box==null) {
			box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(10));
			(new VControl("unitsPerEm","unitsPerEm",this,null)).pinInto(box);
			(new VControl("xMin","xMin for all glyphs",this,VControl.USHORT)).pinInto(box);
			(new VControl("yMin","yMin for all glyphs",this,VControl.USHORT)).pinInto(box);
			(new VControl("xMax","xMax for all glyphs",this,VControl.USHORT)).pinInto(box);
			(new VControl("yMax","yMax for all glyphs",this,VControl.USHORT)).pinInto(box);
			(new VControl("macStyle","Mac font style","STYLE_",this,VControl.USHORT)).pinInto(box);
			view.setLayout(new FlowLayout(FlowLayout.LEADING));
			view.add(Box.createHorizontalStrut(10));
			view.add(box);			
		}
		return spanel;
	}
	
	public void fixChecksum(RandomAccessFile ttf) throws IOException
	{
		ttf.seek(0);
		int len = (int) ttf.length();
		byte[] buffer = new byte[ len + TTFTables.PADDING ]; /* padding to 4 */
		Arrays.fill(buffer,(byte)0);
		ttf.readFully(buffer, 0, len);
		int sum = TTFDirEntry.calcTableChecksum(buffer, len);
		int checksum = 0xB1B0AFBA - sum;
		ttf.seek(this.my_offset+8);
		ttf.writeInt(checksum);
	}
}
