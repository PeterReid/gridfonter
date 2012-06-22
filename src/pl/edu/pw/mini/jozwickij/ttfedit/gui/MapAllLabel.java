package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.GlyphFactory;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_cmapFormat;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_cmapMap;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComposite;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class MapAllLabel extends JLabel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private final static String ADD_MAPPINGS = "<html><a href=#>[Add all missing Polish characters mappings]</a></html>";
	private Vector<TTFTable_cmapMap> cmap = null;
	private TTFTable_cmapFormat cmapFormat = null;
	private JPanel view = null;
	private Font font = null;

	public MapAllLabel(Font f, Vector<TTFTable_cmapMap> map, TTFTable_cmapFormat format, JPanel jview) {
		this.cmap = map;
		this.cmapFormat = format;
		this.view = jview;
		this.font = f;
		this.setText(ADD_MAPPINGS);
		this.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		for (TTFTable_cmapMap map : cmap) {
			try {
				if (cmapFormat.getGlyphIdForChar(map.ch)!=0)
					continue;
				int c1 = cmapFormat.getGlyphIdForChar(map.c1);
				int c2 = cmapFormat.getGlyphIdForChar(map.c2);
				Debug.println("Trying to create new glyph from "+map.c1+" ("+c1+") and "+c2,this);
				
				if (c1>0 && c2>0) {
					int[] cs = { c1, c2, map.flags };
					TTFTable_glyfComposite gl = GlyphFactory.createCompositeGlyph(font,cmapFormat.ttfTables, map.name, cs, true);
					if (gl!=null)
						cmapFormat.injectGlyphMapping(map.ch, gl.getIndex());				
				}
				else if (c1>0) {
					TTFTable_glyfGeneric gl = GlyphFactory.createGlyph(font,cmapFormat.ttfTables, map.name, c1, true);
					if (gl!=null)
						cmapFormat.injectGlyphMapping(map.ch, gl.getIndex());
				}
				else {
					TTFTable_glyfSimple gl = GlyphFactory.createSimpleGlyph(font,cmapFormat.ttfTables, map.name, true);
					if (gl!=null)
						cmapFormat.injectGlyphMapping(map.ch, gl.getIndex());
				}
			}
			catch (Exception ex) {
				Util.showException(ex, null, "Error while adding new mapping '"+map.ch+"'");
			}
		}
		for (Component cmpt : view.getComponents()) {
			if (cmpt instanceof MapLabel) {
				((MapLabel)cmpt).updateText();
			}
		}
		view.remove(this);
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
