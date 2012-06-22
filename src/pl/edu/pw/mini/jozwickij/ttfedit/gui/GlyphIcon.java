package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;

public class GlyphIcon extends ImageIcon {
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	private TTFTable_glyfGeneric glyf = null;

	public GlyphIcon(TTFTable_glyfGeneric glf, int numGlyphs) {
		super();
		glyf = glf;
		int size = this.getSizeForGlyph(numGlyphs);
		image = new BufferedImage(	size, size, BufferedImage.TYPE_INT_ARGB );
		drawIcon();
		this.setImage(image);
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
	
	private void drawIcon() {
		if (image.getWidth()<2)
			return;
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,image.getWidth(),image.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (glyf instanceof TTFTable_glyfSimple) {
			TTFTable_glyfSimple gl = (TTFTable_glyfSimple)glyf;
			for (GeneralPath p :gl.getContours()) {
				g2d.setTransform(gl.getViewTransform(image.getWidth(),image.getHeight()));
				g2d.draw(p);
			}
		}
		g2d.dispose();
	}

}
