package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_post;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;

public class GlyphBox extends JLabel {

	private static final long serialVersionUID = 1L;
	private EtchedBorder eb = new EtchedBorder();
	private Border bb = BorderFactory.createLineBorder(LiteColors.BLUE, 2);
	private GlyphBox thisbox = this;
	private GlyphBoxIcon icon = null;
		
	public GlyphBox(final TTFTable_glyfGeneric glf, final GlyphEditorFrame glyphEditFrame, int numGlyphs, int i, Font f) {
		super( makeLabel(numGlyphs,i) );
		this.setIcon(icon=new GlyphBoxIcon(glf, numGlyphs));
		this.setBorder(eb);
		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()>1) {
					glyphEditFrame.activate(glf, thisbox);
				}				
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				thisbox.setBorder(bb);
			}
			public void mouseExited(MouseEvent e) {
				thisbox.setBorder(eb);
			}
		});
		TTFTable_post post = (TTFTable_post) glf.getFontTables().get(TTFTables.POST);
		this.setToolTipText( post.getGlyphName(i) );
	}
	
	private static String makeLabel(int size, int c) {
		String s = c+"";
		for (int i=(c+"").length(); i<(size+"").length(); i++) {
			s="0"+s;
		}
		return s;
	}
	
	public void updateIcon() {
		icon.updateImage();
	}
}
