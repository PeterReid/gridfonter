package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tools.UndoRedo;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class GlyphEdit extends JLabel implements AdjustmentListener, MouseWheelListener {

	public final static int BORDER = 0;
	public final static int EDIT_UPDATE = 1;
	public final static int EDIT_ADDPT	= 2;
	public final static int EDIT_CONTOUR= 4;
	public final static int EDIT_DELETE = 8;
	private static final long serialVersionUID = 1L;
	private GlyphEdit _this = this;
	
	private GlyphEditIcon icon = null;
	private UndoRedo undo = null;
	private TTFTable_glyfGeneric loadedGlyph = null;
	private Runnable updCallback = new Runnable() {
		public void run() { repaint(); }
	};
	private Runnable focusCallback = new Runnable() {
		public void run() { 
			if (!HelpWindow.isFocusedWindow() && !ScannerWindow.isFocusedWindow())
				focus(_this);			
		}
	};
	private int editState = EDIT_UPDATE;
	private KeyListener keyListener = null;
	
	public GlyphEdit(JComponent frame) {
		this.setRequestFocusEnabled(true);
		this.setFocusable(true);
		this.registerKeyBindings(frame);
		this.registerKeyBindings(this);
	}
	
	private void registerKeyBindings(JComponent cmpnt) {
		if (keyListener==null)
			keyListener = new KeyListener() {
				public void keyTyped(KeyEvent e) {}
				public void keyPressed(KeyEvent e) {
					if ( e.isControlDown() ) {
						if (e.getKeyCode() == KeyEvent.VK_Z) {
							undo();
							e.consume();
						}
						else if (e.getKeyCode() == KeyEvent.VK_Y) {
							redo();
							e.consume();
						}
					}
				}
				public void keyReleased(KeyEvent e) {}
		};
		Util.installKeyListener(cmpnt,keyListener);
	}
	
	protected void unregisterKeyBindings(JComponent cmpnt) {
		Util.uninstallKeyListener(cmpnt,keyListener);
	}
	
	protected void focus(JComponent cmpnt) {
		cmpnt.setRequestFocusEnabled(true);
		cmpnt.setFocusable(true);
		cmpnt.requestFocusInWindow();
		cmpnt.requestFocus();
	}
					
	public void loadGlyph(TTFTable_glyfGeneric glf) {
		if (icon==null)
			icon = new GlyphEditIcon();				
		if (undo==null)
			undo = new UndoRedo();
		this.undo.loadGlyph(glf);
		this.icon.loadGlyph(glf, updCallback, focusCallback, undo);
		this.loadedGlyph = glf;
		this.icon.setEditState(EDIT_UPDATE);
		this.setIcon(icon);
		this.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		this.focus(this);
		this.addMouseMotionListener(icon);
		this.addMouseListener(icon);
	}

	public void setTransform(AffineTransform zoom) {
		icon.setTransform(zoom, 0);		
		this.repaint();
	}
	
	public void addTransform(AffineTransform zoom) {
		icon.addTransform(zoom);
		this.repaint();
	}

	public void setFitTransform(Dimension size) {
		icon.setFitTransform(size);
		this.repaint();		
	}
	
	public void unloadGlyph() {
		this.removeMouseListener(icon);
		this.removeMouseMotionListener(icon);
		this.setIcon(null);
		this.loadedGlyph.fixBoundingBox();
		this.loadedGlyph = null;
		if (undo!=null)
			undo.purge();
	}
	
	public TTFTable_glyfGeneric getGlyph() {
		return this.loadedGlyph;
	}

	public void setEditorAddMode(boolean b) {
		this.editState = b ? EDIT_ADDPT : EDIT_UPDATE;
		if (this.icon!=null) {
			icon.setEditState(editState);
		}
	}

	public void setEditorContourMode(boolean b) {
		this.editState = b ? EDIT_CONTOUR : EDIT_UPDATE;
		if (this.icon!=null) {
			icon.setEditState(editState);
		}
	}

	public void setEditorDelMode(boolean b) {
		this.editState = b ? EDIT_DELETE : EDIT_UPDATE;
		if (this.icon!=null) {
			icon.setEditState(editState);
		}
	}

	public void resetEditorMode() {
		this.editState = EDIT_UPDATE;		
	}

	public boolean getEditorContourMode() {
		return (this.editState == EDIT_CONTOUR);
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getAdjustable().getOrientation()==Adjustable.HORIZONTAL) {
			icon.setDrawOffsetX(e.getValue());
		}
		else if (e.getAdjustable().getOrientation()==Adjustable.VERTICAL) {
			icon.setDrawOffsetY(e.getValue());
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int dl = e.getUnitsToScroll()*DefaultProperties.SCROLL_AMOUNT;
		double sc = dl / icon.getScale();
		icon.addDrawOffsetY((int) sc);
	}

	public void undo() {
		if (undo!=null && undo.undo()) {
			icon.requestRepaint();
		}		
	}

	public void redo() {
		if (undo!=null && undo.redo()) {
			icon.requestRepaint();
		}		
	}
}
