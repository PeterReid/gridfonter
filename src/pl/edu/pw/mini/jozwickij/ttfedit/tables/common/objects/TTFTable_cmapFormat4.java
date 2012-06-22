package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_OS_2;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_cmapFormat4 extends TTFTable_cmapFormat {
	public int length = 0; 
	public int language = 0;		//language-independent 	 
	public int segCountX2 = 0; 		//2 * segCount 	 
	public int searchRange = 0; 	//2 * (2**FLOOR(log2(segCount))) 	 
	public int entrySelector = 0;	//log2(searchRange/2) 	 
	public int rangeShift = 0; 		//(2 * segCount) - searchRange 	 
	public int[] endCode = null; 	//[segCount]
	public int reservedPad = 0;		//This value should be zero 	
	public int[] startCode = null; 	//[segCount]
	public int[] idDelta = null; 	//[segCount]
	public int[] idRangeOffset = null; //[segCount] Offset in bytes to glyph indexArray, or 0 	 
	public int[] glyphIndexArray = null;
	
	public TTFTable_cmapFormat4(RandomAccessFile ttf, int off, int lengthBody, Map<String, TTFTable> tables) throws Exception {
		super(ttf,off,lengthBody, tables);
		this.format = ttf.readUnsignedShort();
		this.length = ttf.readUnsignedShort();
		this.language = ttf.readUnsignedShort();
		this.segCountX2 = ttf.readUnsignedShort();
		this.searchRange = ttf.readUnsignedShort();
		this.entrySelector = ttf.readUnsignedShort();
		this.rangeShift = ttf.readUnsignedShort();
		this.endCode = new int[segCountX2/2];
		for (int i=0; i<endCode.length; i++) {
			endCode[i] = ttf.readUnsignedShort();
		}
		this.reservedPad = ttf.readUnsignedShort();
		if (reservedPad!=0) {
			Debug.printlnErr("reservedPad violates specification (not zero)",this);
		}
		this.startCode = new int[endCode.length];
		this.idDelta = new int[endCode.length];
		this.idRangeOffset =new int[endCode.length];
		for (int i=0; i<startCode.length; i++) {
			startCode[i] = ttf.readUnsignedShort();
		}
		for (int i=0; i<idDelta.length; i++) {
			idDelta[i] = ttf.readUnsignedShort();
		}
		for (int i=0; i<idRangeOffset.length; i++) {
			idRangeOffset[i] = ttf.readUnsignedShort();
		}
		int count = (length - (8*segCountX2/2 + 16)) / 2;
        this.glyphIndexArray = new int[count];
        for (int i = 0; i < count; i++) {
            this.glyphIndexArray[i] = ttf.readUnsignedShort();
        }
        this.ttfTables = tables;        
	}

	@Override
	public int getGlyphIdForChar(int ch) {
		int i = 0;
		try {
			int segCount = this.segCountX2/2;
            for (i = 0; i < segCount; i++) {
                if (this.endCode[i] >= ch && this.startCode[i] <= ch) {
                	if (this.idRangeOffset[i]>0) {
                		return this.glyphIndexArray[idRangeOffset[i]/2 + (ch-startCode[i]) - (segCount - i)];
                	}
                    else {
                        return (idDelta[i] + ch) % 65536;
                    }	                    
                }
                else if (this.endCode[i]>=ch) {
                	break;
                }
            }
        }
		catch (Exception e) {
            Debug.printlnErr("(SEGMENT +"+i+") Format 4: error processing char="+ch,this);            
        }
        return 0;
	}

	@Override
	public int write(RandomAccessFile ttf, int off) throws IOException {
		this.prepareWrite(ttf,off);
		ttf.writeShort(this.format);
		ttf.writeShort(this.length=(8*segCountX2/2 + 16 + 2*glyphIndexArray.length));
		ttf.writeShort(this.language);
		ttf.writeShort(this.segCountX2);
		ttf.writeShort(this.searchRange);
		ttf.writeShort(this.entrySelector);
		ttf.writeShort(this.rangeShift);
		for (int i=0; i<endCode.length; i++) {
			ttf.writeShort(endCode[i]);
		}
		ttf.writeShort(this.reservedPad=0);
		for (int i=0; i<startCode.length; i++) {
			ttf.writeShort(startCode[i]);
		}
		for (int i=0; i<idDelta.length; i++) {
			ttf.writeShort(idDelta[i]);
		}
		for (int i=0; i<idRangeOffset.length; i++) {
			ttf.writeShort(idRangeOffset[i]);
		}
		for (int i = 0; i < this.glyphIndexArray.length; i++) {
			ttf.writeShort(this.glyphIndexArray[i]);
        }
		return this.finishWrite(ttf,off);
	}

	@Override
	public String getInfo() {
		return "Unicode compatible CMAP format 4";
	}

	@Override
	public void injectGlyphMapping(int ch, int gid) throws Exception {
		
		Debug.println("Inject of char "+ch+"<->"+gid,this);
		boolean added = false;
		
		int segCount = this.segCountX2/2, i;
        for (i = 0; i < segCount; i++) {
        	if (this.endCode[i] >= ch && this.startCode[i] <= ch) {
        		Debug.println("Adding new CMAPv4 mapping into segment "+i,this);
        		added = this.addToSegment(i, ch, gid, segCount);
        		break;
        	}
        	else if (this.startCode[i] <= ch && i+1 < segCount && this.startCode[i+1] >= ch) {
        		Debug.println("Adding new CMAPv4 segment between existing",this);
        		this.addSegment(i+1, ch, gid);
        		added = true;
        		break;
        	}
        	if (ch <= startCode[i]) {
        		Debug.println("Adding new CMAPv4 segment before all existing",this);
        		this.addSegment(i, ch, gid);
        		added = true;
        		break;
        	}
        }
        if (!added) {
        	Debug.println("Adding new CMAPv4 segment after all existing",this);
        	this.addSegment(i+1, ch, gid);
        	added = true;            	
        }            
        TTFTable_OS_2 os2 = (TTFTable_OS_2) this.ttfTables.get(TTFTables.OS_2);
        if (os2!=null) {
			os2.ulCharRange1 |= TTFTable_OS_2.UR1_LATIN_EXT_A;
			os2.ulCodePageRange1 |= TTFTable_OS_2.MS_CP_LATIN2;
			fixOS2(ch);
			os2.notifyFieldChanges();
        }
	}
	
	protected void debugGIA() {
		for (int i=0; i < this.glyphIndexArray.length; i++) {
        	Debug.print("("+i+")"+this.glyphIndexArray[i]+"-");        	
        }
		Debug.println("");
	}
	
	private boolean addToSegment(int i, int ch, int gid, int segCount) throws Exception {
		if (this.idRangeOffset[i]>0) {
			int iadd = idRangeOffset[i]/2 + (ch-startCode[i]) - (segCount - i);
			if (this.glyphIndexArray[iadd] > 0)
				throw new Exception("Trying to overwrite already existing mapping of " + this.glyphIndexArray[iadd] + "with char "+ch);
			this.glyphIndexArray[iadd]=gid;
			return true;
		}
		else if (this.idDelta[i] > 0) {
			throw new Exception("Unexpected CMAP segment["+i+"] with idDelta!");
		}
		return false;
	}
	
	private void addSegment(int num, int ch, int gid) {
		endCode = Util.makeRoom(endCode, num, 1, 0);
		startCode = Util.makeRoom(startCode, num, 1, 0);
		this.startCode[num] = ch;
		this.endCode[num] = ch+GID_POOL-1;
		if (num + 1 < segCountX2/2 ) {
			if (this.startCode[num+1] < this.endCode[num]) {
				this.endCode[num] = this.startCode[num+1];
			}				
		}
		
		idDelta = Util.makeRoom(idDelta, num, 1, 0);
		idRangeOffset = Util.makeRoom(idRangeOffset, num, 1, 0);
		int sadd = glyphIndexArray.length;
		glyphIndexArray = Util.makeRoom(glyphIndexArray, sadd, endCode[num]-startCode[num]+1, 0);		
		glyphIndexArray[sadd] = gid;
		
		segCountX2 += 2;
		searchRange = (int) Math.pow(2.0, Math.floor(Util.Math_log2(segCountX2 >> 1))+1);
		entrySelector = (int) Util.Math_log2(searchRange >> 1);
		rangeShift = 2*segCountX2/2 - searchRange;
		
		idRangeOffset[num] = 2* (sadd + segCountX2/2 - num);
		
		for (int i=0; i < num; i++) {
			if (this.idRangeOffset[i] > 0) {
				this.idRangeOffset[i] += 2;
			}
		}
	}
} 