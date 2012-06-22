package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_OS_2;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_cmapFormat12 extends TTFTable_cmapFormat {
	
	private final static long UINT_MASK = (1L << 32)-1L;
	static class Group {
		public long startCharCode = 0;
		public long endCharCode = 0;
		public long startGlyphCode = 0;
	}
	public int length = 0;
	public int language = 0;
	public int nGroups = 0;
	public Group groups[] = null;
	
	public TTFTable_cmapFormat12(RandomAccessFile ttf, int off, int lengthBody, Map<String, TTFTable> tables) throws Exception {
		super(ttf,off,lengthBody, tables);
		this.format = ttf.readInt();
		this.length = (int) (ttf.readInt() & UINT_MASK);
		this.language = (int) (ttf.readInt() & UINT_MASK);
		this.nGroups = (int) (ttf.readInt() & UINT_MASK);
		this.groups = new Group[nGroups];
		for (int i=0; i< this.nGroups; i++) {
			this.groups[i] = new Group();
			this.groups[i].startCharCode = ttf.readInt() & UINT_MASK;
			this.groups[i].endCharCode = ttf.readInt() & UINT_MASK;
			this.groups[i].startGlyphCode = ttf.readInt() & UINT_MASK;
		}
		this.ttfTables = tables;
	}
	
	@Override
	public int write(RandomAccessFile ttf, int off) throws IOException {
		this.prepareWrite(ttf, off);
		ttf.writeInt(this.format);
		ttf.writeInt(this.length = (16 + 12*groups.length) );
		ttf.writeInt(this.language);
		ttf.writeInt(this.nGroups = groups.length);
		for (int i=0; i < groups.length; i++) {
			ttf.writeInt((int) this.groups[i].startCharCode);
			ttf.writeInt((int) this.groups[i].endCharCode);
			ttf.writeInt((int) this.groups[i].startGlyphCode);
		}
		return this.finishWrite(ttf, off);
	}
	
	@Override
	public String getInfo() {
		return "Unicode compatible CMAP format 12.0";
	}

	@Override
	public int getGlyphIdForChar(int ch) {
		for (int i=0; i < this.groups.length; i++) {
			if (groups[i].startCharCode <= ch && ch <= groups[i].endCharCode) {
				return (int) (groups[i].startGlyphCode + ch - groups[i].startCharCode);
			}
			else if (ch < groups[i].endCharCode && ch < groups[i].startCharCode) {
				break;
			}
		}		
		return 0;
	}

	@Override
	public void injectGlyphMapping(int ch, int glyphID) throws Exception {
		Group grp = new Group();
		grp.startCharCode = grp.endCharCode = ch;
		grp.startGlyphCode = glyphID;
		int inject=0;
		for (int i=1; i < this.groups.length; i++) {
			if (groups[i-1].endCharCode < ch && ch < groups[i].startCharCode) {
				inject = i;
				break;
			}
			else if (groups[0].startCharCode > ch) {
				inject = 0;
				break;
			}
			if (i==this.groups.length-1)
				inject = groups.length;
		}
		groups = Util.makeRoom(Group.class, groups, inject, 1, grp);
		nGroups++;
		TTFTable_OS_2 os2 = (TTFTable_OS_2) this.ttfTables.get(TTFTables.OS_2);
		if (os2!=null) {
			os2.ulCharRange1 |= TTFTable_OS_2.UR1_LATIN_EXT_A;
			os2.ulCodePageRange1 |= TTFTable_OS_2.MS_CP_LATIN2;
			fixOS2(ch);
			os2.notifyFieldChanges();
		}
	}	
}
