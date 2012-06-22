package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Font;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;

public class GlyphFactory {
	
	public static TTFTable_glyfSimple createSimpleGlyph(Font f, Map<String, TTFTable> tables, String name, boolean insert) {
		TTFTable_glyfSimple gl = new TTFTable_glyfSimple(tables);
		TTFTable_glyf glyfTab = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		if (glyfTab==null)
			return null;
		TTFTable_glyfSimple template = null;
		
		for (TTFTable_glyfGeneric g : glyfTab.glyphs) {
			if (g instanceof TTFTable_glyfSimple) {
				template = (TTFTable_glyfSimple) g;
				template.cloneBounds(gl);
				break;
			}
		}		
		if (insert) {
			glyfTab.insertGlyph(gl,name,f);
		}
		return gl;
	}
	
	public static TTFTable_glyfGeneric createGlyph(Font f, Map<String, TTFTable> tables, String name, int c1, boolean insert) {
		TTFTable_glyf glyfTab = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		if (glyfTab==null)
			return null;
		TTFTable_glyfGeneric gl = glyfTab.glyphs.get(c1).clone();
		if (insert) {
			glyfTab.insertGlyph(gl,name,f);
		}
		return gl;
	}
	
	public static TTFTable_glyfComposite createCompositeGlyph(Font f, Map<String, TTFTable> tables, String name, int[] cs, boolean insert) {
		TTFTable_glyfComposite gl = new TTFTable_glyfComposite(tables);
		TTFTable_glyf glyfTab = (TTFTable_glyf) tables.get(TTFTables.GLYF);
		if (glyfTab==null)
			return null;
		TTFTable_glyfGeneric parent = glyfTab.glyphs.get(cs[0]);
		TTFTable_glyfGeneric addon  = glyfTab.glyphs.get(cs[1]);
		parent.cloneBounds(gl);
		
		TTFTable_glyfComponent c1 = new TTFTable_glyfComponent(cs[0], tables);
		TTFTable_glyfComponent c2 = new TTFTable_glyfComponent(cs[1], tables);
		int flags = cs[cs.length-1];
		
		if ( (flags & TTFTable_cmapMap.SHIFT_FIX_YMIN) != 0 ) {
			int dy = addon.getHeight() + Math.max(addon.yMax - parent.yMin, 0);
			gl.yMax -= dy;
		}
		if ( (flags & TTFTable_cmapMap.SHIFT_SECOND_BY_HALF_FIRST_DOWN) != 0 ) {
			int dy = - parent.getHeight()/2;
			c2.doShift(0, dy);
		}
		else if ( (flags & TTFTable_cmapMap.SHIFT_SECOND_BY_HALF_FIRST_UP) != 0 ) {
			int dy = + parent.getHeight()/2;
			c2.doShift(0, dy);
		}
		else {
			gl.xMin = Math.min(parent.xMin, addon.xMin);
			gl.xMax = Math.max(parent.xMax, addon.xMin);
			gl.yMin = Math.min(parent.yMin, addon.yMin);
			gl.yMax = Math.max(parent.yMax, addon.yMax);
		}
		
		gl.components.add(c1);
		gl.components.add(c2);
			
		if (insert) {
			glyfTab.insertGlyph(gl,name,f);			
		}
		return gl;
	}	
}
