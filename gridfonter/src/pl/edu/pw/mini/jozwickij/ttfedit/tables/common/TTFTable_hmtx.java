package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_hmtx extends TTFTable {
	
	static class LongHorMetric {
		public int advanceWidth = 0;
		public int lsb = 0;
	};
	
	private LongHorMetric[] hMetrics;			/* The value numOfLongHorMetrics comes from the 'hhea' table. If the font is monospaced, only one entry need be in the array but that entry is required. */
	private int[] leftSideBearing = null;		/* FWord. Here the advanceWidth is assumed to be the same as the advanceWidth for the last entry above. The number of entries in this array is derived from the total number of glyphs minus numOfLongHorMetrics. This generally is used with a run of monospaced glyphs (e.g. Kanji fonts or Courier fonts). Only one run is allowed and it must be at the end. */
	private Map<String,TTFTable> ttfTables = null;
   
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		TTFTable hhea = tables.get(TTFTables.HHEA);
		TTFTable maxp = tables.get(TTFTables.MAXP);
		if (hhea==null || maxp==null) {
			return false; /* we need to be processed later */
		}
		hMetrics = new LongHorMetric[((TTFTable_hhea)hhea).numOfLongHorMetrics];
		leftSideBearing = new int[((TTFTable_maxp)maxp).numGlyphs - ((TTFTable_hhea)hhea).numOfLongHorMetrics];
		
		for (int i=0; i < hMetrics.length; i++) {
			hMetrics[i] = new LongHorMetric();
			hMetrics[i].advanceWidth = ttf.readUnsignedShort();
			hMetrics[i].lsb = ttf.readShort();
		}
		for (int i=0; i < leftSideBearing.length; i++) {
			leftSideBearing[i] = ttf.readShort();
		}
		ttfTables = tables;
		return true;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		for (int i=0; i < hMetrics.length; i++) {
			ttf.writeShort(	hMetrics[i].advanceWidth );
			ttf.writeShort( hMetrics[i].lsb );
		}
		
		TTFTable_hhea hhea = (TTFTable_hhea) tables.get(TTFTables.HHEA);
		TTFTable_maxp maxp = (TTFTable_maxp) tables.get(TTFTables.MAXP);
		int lsbLength = maxp.numGlyphs - hhea.numOfLongHorMetrics;
		if (leftSideBearing.length < lsbLength) {
			leftSideBearing = Util.makeRoom(leftSideBearing, leftSideBearing.length, lsbLength - leftSideBearing.length, 0);
		}
		
		for (int i=0; i < lsbLength; i++) {
			ttf.writeShort( leftSideBearing[i] );
		}
		this.finishWrite(ttf);
		return true;	
	}
	
	public void validateGlyphData(TTFTable_glyfGeneric gl) {
		int pos = gl.getIndex();
		if (pos < this.hMetrics.length) {
			hMetrics[pos].lsb = gl.xMin;
			hMetrics[pos].advanceWidth = gl.getWidth();
		}			
	}

	public void insertGlyphData(TTFTable_glyfGeneric gl) {
		TTFTable_hhea hhea = (TTFTable_hhea) ttfTables.get(TTFTables.HHEA);
		hhea.numOfLongHorMetrics++;
		LongHorMetric lhm = new LongHorMetric();
		lhm.lsb = gl.xMin;
		lhm.advanceWidth = gl.getWidth();
		this.hMetrics = Util.makeRoom(LongHorMetric.class, hMetrics, hMetrics.length, 1, lhm);		
	}	
}
