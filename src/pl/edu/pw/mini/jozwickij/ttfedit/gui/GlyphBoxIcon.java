package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComponent;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComposite;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;

public class GlyphBoxIcon extends ImageIcon {
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	private TTFTable_glyfGeneric glyf = null;
	public final static boolean FILL_GLYPH = true;

	public GlyphBoxIcon(TTFTable_glyfGeneric glf, int numGlyphs) {
		super();
		glyf = glf;
		int size = this.getSizeForGlyph(numGlyphs);
		if (!DefaultProperties.LOW_MEM)
			image = new BufferedImage(	size, size, BufferedImage.TYPE_BYTE_GRAY );
		requestRepaint();
		if (image!=null)
			this.setImage(image);
	}
	
	private void requestRepaint() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				drawIcon();								
			}
		});
	}
	
	private int getSizeForGlyph(int numGlyphs) {
		int size = DefaultProperties.ICON_SIZE;
		if (numGlyphs>20000) {
			size = size/3;
		}
		else if (numGlyphs>10000) {
			size = size/2;
		}
		return size;
	}
	
	public static void testDrawGlyph(TTFTable_glyfGeneric gGlyph) {
		if (gGlyph instanceof TTFTable_glyfSimple) {
			TTFTable_glyfSimple gl = (TTFTable_glyfSimple)gGlyph;
			gl.getContours();
		}
		else if (gGlyph instanceof TTFTable_glyfComposite) {
			TTFTable_glyfComposite gl = (TTFTable_glyfComposite)gGlyph;
						
			for (TTFTable_glyfComponent c : gl.components) {
				TTFTable_glyf glyfTab = (TTFTable_glyf)gl.getFontTables().get(TTFTables.GLYF);
				TTFTable_glyfGeneric cGlyph = glyfTab.glyphs.get(c.glyphIndex);
				testDrawGlyph(cGlyph);				
			}			
		}
	}
	
	private void drawGlyph(Graphics2D g2d, TTFTable_glyfGeneric gGlyph) {
		
		if (gGlyph instanceof TTFTable_glyfSimple) {
			TTFTable_glyfSimple gl = (TTFTable_glyfSimple)gGlyph;
			//gl.getContours();
			
			if (!FILL_GLYPH) {
				for (GeneralPath p : gl.getContours()) {
					g2d.draw(p);
				}
			}
			else {
				GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
				for (GeneralPath p : gl.getContours()) {
					gp.append(p.getPathIterator(null),!true);					
				}			
				g2d.fill(gp);
			}	
		}
		else if (gGlyph instanceof TTFTable_glyfComposite) {
			TTFTable_glyfComposite gl = (TTFTable_glyfComposite)gGlyph;
			AffineTransform at = g2d.getTransform();
			
			for (TTFTable_glyfComponent c : gl.components) {
				TTFTable_glyf glyfTab = (TTFTable_glyf)gl.getFontTables().get(TTFTables.GLYF);
				TTFTable_glyfGeneric cGlyph = glyfTab.glyphs.get(c.glyphIndex);
				AffineTransform t = (AffineTransform) at.clone();
				t.concatenate(c.getTransform());
				g2d.setTransform(t);
				this.drawGlyph(g2d,cGlyph);
				g2d.setTransform(at);
			}			
		}
	}
	
	private void drawIcon() {
		if (image==null || image.getWidth()<2)
			return;
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,image.getWidth(),image.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform( glyf.getViewTransform(image.getWidth(),image.getHeight()) );
		drawGlyph(g2d,glyf);		
		g2d.dispose();
	}
	
	public void updateImage() {
		drawIcon();		
	}
}
