package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.gui.GlyphBox;
import pl.edu.pw.mini.jozwickij.ttfedit.gui.GlyphBoxIcon;
import pl.edu.pw.mini.jozwickij.ttfedit.gui.GlyphEditorFrame;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_maxpStats;
import pl.edu.pw.mini.jozwickij.ttfedit.util.CachedFileInput;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.RandomAccessInput;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_glyf extends TTFTable {
	
	public Vector<TTFTable_glyfGeneric> glyphs = new Vector<TTFTable_glyfGeneric>();
	private Vector<GlyphBox> glyphBoxes = new Vector<GlyphBox>();
	private GlyphEditorFrame glyphEdit = null;
	private Map<String,TTFTable> ttfTables = null;
		
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String,TTFTable> tables) throws Exception {
		TTFTable loca = tables.get(TTFTables.LOCA);
		if (loca==null) {
			return false; /* we need to be processed later */
		}
		
		if (DefaultProperties.USE_CACHE) {
			 /* usefull with large fonts > 1MB */
			RandomAccessInput cis = new CachedFileInput(ttf, offset);
			boolean ret = readFromCache(cis, offset, length, checksum, tables);
			cis.close();
			cis = null;
			return ret;
		}

		int offsets[] = ((TTFTable_loca)loca).offsets;
		long fp = ttf.getFilePointer();
		Debug.println("There will be "+offsets.length+" glyphs according to LOCA",this);
								
		for (int i=0; i<offsets.length-1; i++) {
			ttf.seek(fp+offsets[i]);
			TTFTable_glyfGeneric.readGlyph((RandomAccessInput)ttf, glyphs, tables,offsets[i]==offsets[i+1]);			
		}
		this.ttfTables = tables;
		return true;
	}
	
	public boolean readFromCache(RandomAccessInput ttf, int offset, int length, int checksum, Map<String,TTFTable> tables) throws Exception {
		TTFTable loca = tables.get(TTFTables.LOCA);
		int offsets[] = ((TTFTable_loca)loca).offsets;
		long fp = ttf.getFilePointer();
		Debug.println("There will be "+(offsets.length-1)+" glyphs according to cached LOCA",this);
		
		for (int i=0; i<offsets.length-1; i++) {
			ttf.seek(fp+offsets[i]);
			TTFTable_glyfGeneric.readGlyph(ttf, glyphs, tables, offsets[i]==offsets[i+1]);			
		}
		this.ttfTables = tables;
		return true;
	}

	public void testGlyphs() throws Exception {
		int cnt = 0;
		for (TTFTable_glyfGeneric glf : glyphs) {
			TTFTable_post post = (TTFTable_post) glf.getFontTables().get(TTFTables.POST);
			post.getGlyphName(cnt);
			GlyphBoxIcon.testDrawGlyph(glf);
		}	
		
	}
	@Override
	public JComponent getView(Component c, Font f) {
		if (c instanceof JPanel) {
			try {
				testGlyphs();				
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			return null;
		}
		try {
			this.setupView();
			if (this.glyphBoxes.size()==0) {
				
				if (glyphEdit==null) {
					glyphEdit = new GlyphEditorFrame(c,view);
				}
				view.setLayout(new GridLayout(0,DefaultProperties.GRID_X));
				view.setBorder(new EmptyBorder(5,5,5,5));
				int cnt=0;
							
				for (TTFTable_glyfGeneric glf : glyphs) {			
					view.add(new GlyphBox(glf,glyphEdit,glyphs.size(),cnt++,f) );
				}				
			}
		}
		catch (OutOfMemoryError oom) {
			Util.showException(oom, null, "Try to increase -Xmx JVM startup parameter!");
		}
		catch (Exception e) {
			Util.showException(e, null, "Exception");
		}
		return this.spanel;
	}
	
	public int insertGlyph(TTFTable_glyfGeneric gl, String name, Font f) {
		try {
			gl.fixBoundingBox();
			glyphs.add(gl);
			TTFTable_maxp maxp = (TTFTable_maxp) ttfTables.get(TTFTables.MAXP);
			maxp.numGlyphs += 1;
			TTFTable_post post = (TTFTable_post) this.ttfTables.get(TTFTables.POST);
			post.insertGlyphName(gl.getIndex(), name);
			TTFTable_hmtx hmtx = (TTFTable_hmtx) this.ttfTables.get(TTFTables.HMTX);
			hmtx.insertGlyphData(gl);
			view.add( new GlyphBox(gl,glyphEdit,glyphs.size(),view.getComponentCount(), f) );
			SwingUtilities.updateComponentTreeUI(view);			
		}
		catch (OutOfMemoryError oom) {
			Util.showException(oom, null, "Try to increase -Xmx JVM startup parameter!");
		}
		catch (Exception e) {
			Util.showException(e, null, "Exception");
		}
		return glyphs.size()-1;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		TTFTable_loca loca = (TTFTable_loca) tables.get(TTFTables.LOCA);
		TTFTable_maxpStats maxpStats = new TTFTable_maxpStats(tables);
		
		if (loca.offsets.length!=this.glyphs.size()+1) {
			loca.offsets = new int[this.glyphs.size()+1];
		}
		for (int i=1; i < loca.offsets.length; i++) {
			TTFTable_glyfGeneric gl = this.glyphs.get(i-1);
			loca.offsets[i-1] = (int) (this.walign4(ttf) - this.my_offset);
			gl.writeToFile(ttf, tables);
			maxpStats.validate(gl);
		}
		this.walign4(ttf);
		this.finishWrite(ttf);
		loca.offsets[this.glyphs.size()] = this.my_length;
		TTFTable head = tables.get(TTFTables.HEAD);
		
		if (this.my_length >= (Short.MAX_VALUE-2)*2)
			((TTFTable_head)head).indexToLocFormat = 1;
		else
			((TTFTable_head)head).indexToLocFormat = 0;
		
		maxpStats.updateMaxp();
		return true;
	}
	
	@Override
	public boolean isViewUserFriendly() {
		return true;
	}	
}
 