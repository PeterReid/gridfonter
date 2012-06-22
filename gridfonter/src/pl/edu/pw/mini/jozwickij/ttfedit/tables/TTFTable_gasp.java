package pl.edu.pw.mini.jozwickij.ttfedit.tables;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

/** MSFT extension
 * - see http://www.microsoft.com/typography/OTSPEC/gasp.htm
 */
public class TTFTable_gasp extends TTFTable {
	public final static int GASP_DOGRAY = 0x0002;
	public final static int GASP_GRIDFIT= 0x0001;
	public final static int MAXPPM_SENTINEL = 0xFFFF;
	
	public static class GaspRange {
		int rangeMaxPPEM = MAXPPM_SENTINEL;					// USHORT Upper limit of range, in PPEM
		int	rangeGaspBehavior =GASP_DOGRAY|GASP_GRIDFIT;	// USHORT Flags describing desired rasterizer behavior.
	}
	
	public int version = 0;		// USHORT Version number (set to 0)
	public int numRanges = 0;	// USHORT Number of records to follow
	public GaspRange[] gaspRanges = null;
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.version = ttf.readUnsignedShort();
		this.numRanges = ttf.readUnsignedShort();
		this.gaspRanges = new GaspRange[numRanges];
		for (int i=0; i<numRanges; i++) {
			this.gaspRanges[i] = new GaspRange();
			this.gaspRanges[i].rangeMaxPPEM = ttf.readUnsignedShort();
			this.gaspRanges[i].rangeGaspBehavior = ttf.readUnsignedShort();
		}
		return true;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		if (this.gaspRanges==null || !DefaultProperties.PRESERVE_TTF_ASM) {
			Debug.printlnErr("Using GASP hack for antialiasing",this);
			this.gaspRanges = new GaspRange[1];
			this.gaspRanges[0] = new GaspRange();			
		}
		ttf.writeShort(this.version=0);
		ttf.writeShort(this.numRanges=gaspRanges.length);
		for (int i=0; i < gaspRanges.length; i++) {
			ttf.writeShort(this.gaspRanges[i].rangeMaxPPEM);
			ttf.writeShort(this.gaspRanges[i].rangeGaspBehavior);
		}
		this.finishWrite(ttf);
		return true;
	}	
}
