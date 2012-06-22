package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.hinting.Program;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_maxp extends TTFTable {
	public int version = 0x00010000; 	//(1.0)
	public int numGlyphs = 0;			//	the number of glyphs in the font
	public int maxPoints = 0;			// 	points in non-compound glyph
	public int maxContours = 0;			// 	contours in non-compound glyph
	public int maxComponentPoints = 0;	//	points in compound glyph
	public int maxComponentContours = 0;// 	contours in compound glyph
	public int maxZones = 2;			//	set to 2
	public int maxTwilightPoints = 0;	//	points used in Twilight Zone (Z0)
	public int maxStorage = 0;			//	number of Storage Area locations
	public int maxFunctionDefs = 0;		//	number of FDEFs
	public int maxInstructionDefs = 0;	// 	number of IDEFs
	public int maxStackElements = 0;	// 	maximum stack depth
	public int maxSizeOfInstructions= 0;//	byte count for glyph instructions
	public int maxComponentElements = 0;//	number of glyphs referenced at top level
	public int maxComponentDepth = 1;	// 	levels of recursion, set to 0 if font has only simple glyphs
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.version = ttf.readInt();
		this.numGlyphs = ttf.readUnsignedShort();
		this.maxPoints = ttf.readUnsignedShort();
		this.maxContours = ttf.readUnsignedShort();
		this.maxComponentPoints = ttf.readUnsignedShort();
		this.maxComponentContours = ttf.readUnsignedShort();
		this.maxZones = ttf.readUnsignedShort();
		this.maxTwilightPoints = ttf.readUnsignedShort();
		this.maxStorage = ttf.readUnsignedShort();
		this.maxFunctionDefs = ttf.readUnsignedShort();
		this.maxInstructionDefs = ttf.readUnsignedShort();
		this.maxStackElements = ttf.readUnsignedShort();
		this.maxSizeOfInstructions = ttf.readUnsignedShort();
		this.maxComponentElements = ttf.readUnsignedShort();
		this.maxComponentDepth = ttf.readUnsignedShort();
		if (this.numGlyphs > DefaultProperties.LOW_MEM_TRESHOLD) {
			Debug.printlnErr("Enabling low-mem mode",this);
			DefaultProperties.LOW_MEM = true;
		}
		return true;
	}
	
	@Override
	public void notifyWrite(Map<String, TTFTable> tables) {
		TTFTable_glyf glyf = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		this.numGlyphs = glyf!=null ? glyf.glyphs.size() : 0;		
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		if (!DefaultProperties.PRESERVE_TTF_ASM) {
			maxSizeOfInstructions = Program.getInstructionsCount();
		}
		ttf.writeInt(this.version);
		ttf.writeShort(this.numGlyphs);
		ttf.writeShort(this.maxPoints);
		ttf.writeShort(this.maxContours);
		ttf.writeShort(this.maxComponentPoints);
		ttf.writeShort(this.maxComponentContours);
		ttf.writeShort(this.maxZones=2);
		ttf.writeShort(this.maxTwilightPoints);
		ttf.writeShort(this.maxStorage);
		ttf.writeShort(this.maxFunctionDefs);
		ttf.writeShort(this.maxInstructionDefs);
		ttf.writeShort(this.maxStackElements);
		ttf.writeShort(this.maxSizeOfInstructions);
		ttf.writeShort(this.maxComponentElements);
		ttf.writeShort(this.maxComponentDepth);
		this.finishWrite(ttf);
		return true;
	}	
}
