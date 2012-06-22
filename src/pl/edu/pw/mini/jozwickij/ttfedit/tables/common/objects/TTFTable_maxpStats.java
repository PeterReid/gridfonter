package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.hinting.Program;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_maxp;

public class TTFTable_maxpStats {
	
	protected static class MaxpStats {
		public int maxPoints = 0;
		public int maxContours = 0;
		public int maxCompositePoints = 0;
		public int maxCompositeContours = 0;
		public int maxSizeOfInstructions = 0;
		public int maxComponentElements = 0;
		public int maxComponentDepth = 0;
		public int pPoints = 0;
		public int pContours = 0;
		public int pDepth = 0;
	}
	
	private MaxpStats stats = new MaxpStats();
	private Map<String, TTFTable> tables = null;
	private TTFTable_glyf glyfTab = null;
	private TTFTable_maxp maxpTab = null;
	
	public TTFTable_maxpStats(Map<String, TTFTable> ttfTables) {
		this.tables = ttfTables;
		glyfTab = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		maxpTab = (TTFTable_maxp) tables.get(TTFTables.MAXP);
	}
	
	private void validate(TTFTable_glyfGeneric gl, MaxpStats ms) {
		if (gl instanceof TTFTable_glyfSimple) {
			checkSimpleGlyph((TTFTable_glyfSimple) gl, ms);
		}
		else if (gl instanceof TTFTable_glyfComposite) {
			checkCompositeGlyph((TTFTable_glyfComposite) gl, ms);
		}
	}
	
	private void checkSimpleGlyph(TTFTable_glyfSimple gs, MaxpStats ms) {
		int contours = gs.getContours().size();
		int instructions = Program.getInstructionsCount();
		
		if (!DefaultProperties.PRESERVE_TTF_ASM) {
			instructions = gs.instructionLength;
		}		
		ms.pPoints = gs.getPointsCount();
		ms.pContours = contours;
		ms.maxPoints = Math.max(ms.maxPoints, gs.getPointsCount());
		ms.maxContours = Math.max(ms.maxContours, contours);
		ms.maxSizeOfInstructions = Math.max(ms.maxSizeOfInstructions, instructions);
	}
	
	private void checkCompositeGlyph(TTFTable_glyfComposite gc, MaxpStats ms) {
		int points = 0;
		int contours = 0;
		int instructions = Program.getInstructionsCount();
		int componentElems = gc.components.size();
		int componentDepth = 0;
		
		if (DefaultProperties.PRESERVE_TTF_ASM) {
			instructions = (gc.instructions!=null) ? gc.instructions.length : 0;
		}		
		ms.maxSizeOfInstructions = Math.max(ms.maxSizeOfInstructions, instructions);
		ms.maxComponentElements = Math.max(ms.maxComponentElements, componentElems);
		
		MaxpStats myStats = new MaxpStats();
		int childDepth = 0;
		
		for (TTFTable_glyfComponent comp : gc.components) {
			TTFTable_glyfGeneric glyf = glyfTab.glyphs.get( comp.glyphIndex );
			this.validate(glyf, myStats);
			if (glyf instanceof TTFTable_glyfSimple) {
				points += myStats.pPoints;
				contours += myStats.pContours;	
				childDepth = Math.max(childDepth, 1);
			}
			else if (glyf instanceof TTFTable_glyfComposite) {
				points += myStats.pPoints;
				contours += myStats.pContours;
				childDepth = Math.max(childDepth, 1 + myStats.pDepth);
			}
		}
		
		componentDepth += childDepth;
		ms.pPoints = points;
		ms.pContours = contours;
		ms.pDepth = componentDepth;
			
		ms.maxCompositePoints = Math.max(ms.maxCompositePoints, points);
		ms.maxCompositeContours = Math.max(ms.maxCompositeContours, contours);		
		ms.maxComponentDepth = Math.max(ms.maxComponentDepth, componentDepth);
	}
	
	public void validate(TTFTable_glyfGeneric gl) {
		this.validate(gl, this.stats);
	}
	
	public void validateAll() {
		for (TTFTable_glyfGeneric gl : glyfTab.glyphs) {
			validate(gl);
		}
	}
	
	public void updateMaxp() {
		maxpTab.maxPoints = this.stats.maxPoints;
		maxpTab.maxContours = this.stats.maxContours;
		maxpTab.maxComponentPoints = this.stats.maxCompositePoints;
		maxpTab.maxComponentContours = this.stats.maxCompositeContours;
		maxpTab.maxSizeOfInstructions = this.stats.maxSizeOfInstructions;
		maxpTab.maxComponentElements = this.stats.maxComponentElements;
		maxpTab.maxComponentDepth = this.stats.maxComponentDepth;
	}
}
