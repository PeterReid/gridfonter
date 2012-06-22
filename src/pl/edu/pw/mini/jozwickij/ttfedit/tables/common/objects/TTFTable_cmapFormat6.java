package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_OS_2;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_cmapFormat6 extends TTFTable_cmapFormat {
	public int length = 0;
	public int language = 0;
	public int firstCode = 0;
	public int entryCount = 0;
	public int[] glyphIndexArray = null;//[entryCount]
	
	public TTFTable_cmapFormat6(RandomAccessFile ttf, int off, int lengthBody, Map<String, TTFTable> tables) throws Exception {
		super(ttf,off,lengthBody, tables);
		this.format = ttf.readUnsignedShort();
		this.length = ttf.readUnsignedShort();
		this.language = ttf.readUnsignedShort();
		this.firstCode = ttf.readUnsignedShort();
		this.entryCount = ttf.readUnsignedShort();
		this.glyphIndexArray = new int[this.entryCount];
		for (int i=0; i< this.entryCount; i++) {
			this.glyphIndexArray[i] = ttf.readUnsignedShort();
		}
		this.ttfTables = tables;
	}

	@Override
	public int getGlyphIdForChar(int ch) {
		if (ch<firstCode || ch>firstCode+entryCount) {
			return 0;
		}
		else {
			return this.glyphIndexArray[ch-firstCode];
		}
	}

	@Override
	public int write(RandomAccessFile ttf, int off) throws IOException {
		this.prepareWrite(ttf, off);
		ttf.writeShort(this.format);
		ttf.writeShort(this.length = (10 + 2*glyphIndexArray.length) );
		ttf.writeShort(this.language);
		ttf.writeShort(this.firstCode);
		ttf.writeShort(this.entryCount);
		for (int i=0; i < this.glyphIndexArray.length; i++) {
			ttf.writeShort(this.glyphIndexArray[i]);
		}
		return this.finishWrite(ttf, off);
	}
	
	@Override
	public String getInfo() {
		return "Unicode compatible CMAP format 6";
	}

	@Override
	public void injectGlyphMapping(int ch, int gid) throws Exception {
		Debug.println("Inject of char "+ch+"<->"+gid,this);
		if (firstCode <= ch && firstCode + entryCount >=ch) {
			Debug.println("Adding new CMAPv6 entry between existing",this);
			if (this.glyphIndexArray[ch-firstCode] != 0)
				throw new Exception("Place for char "+ch+" is already taken by glyph "+this.glyphIndexArray[ch-firstCode]);
			else {
				this.glyphIndexArray[ch-firstCode] = gid;
				return;
			}			
		}
		else if (ch < firstCode) {
			Debug.println("Adding new CMAPv6 entry before all existing",this);
			glyphIndexArray = Util.makeRoom(glyphIndexArray, 0, firstCode-ch, 0);
			entryCount += (firstCode - ch);
			firstCode = ch;			
		}
		else if (ch > firstCode + entryCount) {
			Debug.println("Adding new CMAPv6 entry after all existing",this);
			glyphIndexArray = Util.makeRoom(glyphIndexArray, ch-firstCode, GID_POOL, 0);
			entryCount += (glyphIndexArray.length - entryCount);			
		}
		this.glyphIndexArray[ch-firstCode] = gid;
		TTFTable_OS_2 os2 = (TTFTable_OS_2) this.ttfTables.get(TTFTables.OS_2);
		if (os2!=null) {
			os2.ulCharRange1 |= TTFTable_OS_2.UR1_LATIN_EXT_A;
			os2.ulCodePageRange1 |= TTFTable_OS_2.MS_CP_LATIN2;
			fixOS2(ch);
			os2.notifyFieldChanges();
		}
	}
}	
 