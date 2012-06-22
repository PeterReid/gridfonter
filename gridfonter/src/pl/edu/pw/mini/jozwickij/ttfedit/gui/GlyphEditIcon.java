package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComponent;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComposite;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;
import pl.edu.pw.mini.jozwickij.ttfedit.tools.UndoRedo;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class GlyphEditIcon extends ImageIcon implements MouseMotionListener, MouseListener {

	public final static int MAXCOORD = Short.MAX_VALUE*3/4;
	public final static int MINCOORD = Short.MIN_VALUE*3/4;
	private static final long serialVersionUID = 1L;
	private final static int POINT_R = 8;
	private final static int RULER_DIV = 16;
	private final static int REDRAW_NOT = 2;
	private final static int REDRAW_NOW = 1;
	private final static int REDRAW_QUEUE = 0;
	
	private BufferedImageWrapper image = null;
	private TTFTable_glyfGeneric glyph = null;
	private AffineTransform transform = null;
	private AffineTransform fontTransform = null;
	private AffineTransform drawTransform = null;
	private Point compositePoint = null;
	private boolean is_dragging = false;
	private Runnable onUpdate = null;
	private Runnable onMouseMove = null;
	private int selectedPoint = -1;
	private int preinsertPoint = -1;
	private int editMode = 0;
	private boolean startContour = false;
	private int ox = 0;
	private int oy = 0;
	private UndoRedo undoRedo = null;		
			
	public void loadGlyph(TTFTable_glyfGeneric glf, Runnable updCallback, Runnable focusCallback, UndoRedo _undoRedo) {		
		glyph = glf;
		if (onUpdate==null) {
			onUpdate = updCallback;
		}
		if (onMouseMove==null) {
			onMouseMove = focusCallback;
		}
		undoRedo = _undoRedo;
		makeImage();		
	}
	
	public void destroy() {
		this.glyph = null;
		this.image = null;
	}
	
	private void makeImage() {
		int em2 = glyph.getEmSize()*2;
		if (image==null || image.getWidth()!=em2) {
			try {
				image = new BufferedImageWrapper( em2, em2, BufferedImage.TYPE_INT_ARGB );
			}
			catch (OutOfMemoryError err) {
				image = new BufferedImageWrapper( em2, em2, BufferedImage.TYPE_BYTE_INDEXED );				
			}
		}
		this.setImage(image);		
	}
	
	private void drawGlyph(Graphics2D g2d, TTFTable_glyfGeneric genGlyph) {
		
		TTFTable_glyfComposite glComposite =	
			(genGlyph instanceof TTFTable_glyfComposite) ? (TTFTable_glyfComposite)genGlyph : null;
		TTFTable_glyfSimple glSimple =	
			(genGlyph instanceof TTFTable_glyfSimple) ? (TTFTable_glyfSimple)genGlyph : null;
			
		if (glSimple!=null) {
			for (GeneralPath gp : glSimple.getContours()) {
				g2d.draw(gp);			
			}
		}
		else if (glComposite!=null) {
			AffineTransform at = g2d.getTransform();
			
			for (TTFTable_glyfComponent c : glComposite.components) {
				TTFTable_glyf glyfTab = (TTFTable_glyf)genGlyph.getFontTables().get(TTFTables.GLYF);
				TTFTable_glyfGeneric gGlyph = glyfTab.glyphs.get(c.glyphIndex);
				AffineTransform t = (AffineTransform) at.clone();
				t.concatenate(c.getTransform());
				g2d.setTransform(t);
				this.drawGlyph(g2d,gGlyph);
				g2d.setTransform(at);
			}			
		}
		else {
			Debug.printlnErr("Invalid glyph passed to EditIcon draw routine",this);
		}
	}
	
	private void drawString(Graphics2D g2d, String string, float x, float y) {
		AffineTransform oldAt = g2d.getTransform();
		Font oldFont = g2d.getFont();
				
		Font f = new Font("Arial",Font.PLAIN,(int) (oldAt.getScaleX()*24));
		g2d.setFont(f);
		double[] srcPts = { x, y };
		double[] dstPts = new double[2];
		oldAt.transform(srcPts, 0, dstPts, 0, 1);		
		g2d.setTransform(AffineTransform.getScaleInstance(1,1));
		g2d.drawString(string, (int)dstPts[0], (int)dstPts[1]);
		
		g2d.setTransform(oldAt);
		g2d.setFont(oldFont);
		f = null;
	}

	private void drawRuler(Graphics2D g, int x1, int y1, int x2, int y2) {
		boolean horizontal = (y2-y1==0);
		int step = (horizontal ? x2-x1 : y2-y1) / RULER_DIV;
		
		if (horizontal) {
			x1+=step/2;
			while (x1<=x2-step/2) {
				g.drawString(x1+"",x1,y1);
				x1+=step;
			}
		}
		else {
			while (y1<=y2) {
				g.drawString(-y1+"",x1,y1);
				y1+=step;
			}
		}		
	}
	
	private void drawControls(Graphics2D g2d) {
		Paint oldPaint = g2d.getPaint();
		Stroke st = g2d.getStroke();
		g2d.setStroke(new BasicStroke(6.0f));
		g2d.setColor(LiteColors.LITE_BLUE_);
		int em = glyph.getEmSize();
		g2d.drawLine(-em,glyph.yMin,em,glyph.yMin);
		g2d.drawLine(-em,glyph.yMax,em,glyph.yMax);
		g2d.drawLine(glyph.xMin,-em,glyph.xMin,em);
		g2d.drawLine(glyph.xMax,-em,glyph.xMax,em);
		g2d.setColor(LiteColors.LITE_RED_);
		g2d.drawLine(0,0, em,0);
		g2d.drawLine(0,em, em,em);
		g2d.drawLine(0,0, 0,em);
		g2d.drawLine(em,0, em,em);
		float dash[] = { 10.0f };
		Stroke sDashed = new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	    g2d.setStroke(sDashed);
		g2d.drawLine(-em,-em, +em,-em);
		g2d.drawLine(-em,+em, +em,+em);
		g2d.drawLine(-em,+em, -em,-em);
		g2d.drawLine(+em,+em, +em,-em);		
		Font oldFont = g2d.getFont();
		Font newFont = new Font("Sans", Font.PLAIN, 36);		
		g2d.setPaint(Color.BLACK);
		g2d.setFont(newFont);
		AffineTransform at = g2d.getTransform();
		AffineTransform oldAt = (AffineTransform) at.clone();
		at.concatenate( AffineTransform.getScaleInstance(1,-1) ); /* flip back */
		g2d.setTransform(at);
		drawRuler(g2d, -em,-em, +em,-em);
		drawRuler(g2d, -em,+em, +em,+em);
		drawRuler(g2d, -em,-em, -em,+em);
		drawRuler(g2d, +em,-em, +em,+em);
		g2d.setFont(oldFont);
		g2d.setPaint(oldPaint);
		g2d.setTransform(oldAt);
		g2d.setStroke(st);
		newFont = null;
	}
	
	private Color getRectColor(TTFTable_glyfSimple gs, int i) {
		if (i==preinsertPoint && editMode == GlyphEdit.EDIT_ADDPT) {
			return Color.GREEN;
		}
		else if (i==selectedPoint) {
			return Color.RED;
		}
		else if (gs.flags.length > i && !TTFTable.bit(gs.flags[i],TTFTable_glyfSimple.ON_CURVE)) {
			return Color.GRAY;
		}
		else {
			return Color.BLACK;
		}		
	}
	
	private void drawPoints(Graphics2D g2d, TTFTable_glyfGeneric glyph2d) {
		TTFTable_glyfComposite glyphComposite =	
			(glyph2d instanceof TTFTable_glyfComposite) ? (TTFTable_glyfComposite)glyph2d : null;
		TTFTable_glyfSimple glyphSimple =	
			(glyph2d instanceof TTFTable_glyfSimple) ? (TTFTable_glyfSimple)glyph2d : null;
		
		Paint oldPaint = g2d.getPaint();
				
		if (glyphSimple!=null) {
			int cont = 0, point = 0;
			for (int i=0; i < glyphSimple.getPointsCount(); i++) {
				Color c = getRectColor(glyphSimple, i);
				g2d.setPaint(c);
				g2d.fillRect(glyphSimple.xs[i]-POINT_R, glyphSimple.ys[i]-POINT_R, 2*POINT_R, 2*POINT_R);
				if (i > glyphSimple.endPtsOfContours[cont]) {
					cont++;
					point = 0;
				}
				drawString(g2d, (1+cont)+"."+(1+point++), glyphSimple.xs[i]+15, glyphSimple.ys[i]+5);
			}				
		}
		else if (glyphComposite!=null) {
			Stroke oldStroke = g2d.getStroke();
			Color oldColor = g2d.getColor();
			float dash[] = { 10.0f, 20.0f };
			Stroke sDashed = new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		    g2d.setStroke(sDashed);
		    g2d.setColor(LiteColors.BLUE_);
		    
		    for (TTFTable_glyfComponent comp : glyphComposite.components) {
		    	g2d.draw(comp.getBoundingBox());
			}
			
			g2d.setStroke(oldStroke);
			g2d.setColor(oldColor);
		}
		
		g2d.setPaint(oldPaint);		
	}
	
	private void drawIcon() {
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,image.getWidth(),image.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform at = AffineTransform.getScaleInstance(1,-1);
		int em = glyph.getEmSize();
		at.translate(+em,-em); /* shift to positive square of 2em x 2em */ 
		if (transform!=null) {
			at.preConcatenate(transform);			
		}
		this.drawTransform = at;
		g2d.setTransform(at);
		this.drawControls(g2d);
		this.drawGlyph(g2d,glyph);
		this.drawPoints(g2d,glyph);
		g2d.dispose();		
	}
	
	public int getWidth() {
		return image.getRealWidth();
	}
	
	public int getHeight() {
		return image.getRealHeight();
	}
	
	public Dimension getSize() {
		return new Dimension( image.getHeight(), image.getWidth() );
	}

	public void setTransform(AffineTransform t, int redraw) {
		transform = t;
		fontTransform = (AffineTransform) t.clone();
		if (redraw==REDRAW_NOT)
			return;
		if (redraw==REDRAW_NOW)
			drawIcon();
		else if (redraw==REDRAW_QUEUE)
			this.requestRepaint();
	}
	
	public void setFitTransform(Dimension size) {
		double sc = (double)Math.min(size.width,size.height) / Math.max(getWidth(), getHeight());
		AffineTransform at = AffineTransform.getScaleInstance(sc,sc);
		setTransform(at, REDRAW_NOW);		
	}

	public void addTransform(AffineTransform zoom) {
		if (transform==null) {
			transform = fontTransform = zoom;			
		}
		else {
			fontTransform.concatenate(zoom);
			transform = (AffineTransform) fontTransform.clone();
			AffineTransform offT = AffineTransform.getTranslateInstance(-ox, -oy);
			transform.concatenate(offT);
		}
		this.requestRepaint();
	}
	
	public static boolean matchesPoint(int x, int y, Point p) {
		return Math.abs(p.x-x)<POINT_R && Math.abs(p.y-y)<POINT_R;
	}
	
	private boolean getMatchingPoint(Point p) {	
		if (this.glyph instanceof TTFTable_glyfComposite) {
			if (is_dragging && selectedPoint != -1) {
				return true; /* do not switch layer */
			}
			for (int i=0; i < glyph.getComposite().components.size(); i++) {
				if (glyph.getComposite().components.get(i).getBoundingBox().contains(p)) {
					this.selectedPoint = i;
					return true;
				}
			}
			return false;
		}
		TTFTable_glyfSimple gl = (TTFTable_glyfSimple) this.glyph;
		
		for (int i=0; i<gl.getPointsCount(); i++) {
			if (matchesPoint(gl.xs[i], gl.ys[i], p)) {
				this.selectedPoint = i;
				return true;
			}
		}
		this.selectedPoint = -1;
		return false;	
	}
	
	protected void requestRepaint() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				drawIcon();
				onUpdate.run();					
			}
		});
	}
	
	private Point getRealPoint(Point pt) {
		try {
			drawTransform.inverseTransform(pt,pt);
		}
		catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		return pt;
	}

	public void mouseDragged(MouseEvent e) {
		
		Point pt = e.getPoint();
		pt.x -= GlyphEdit.BORDER;
		pt.y -= GlyphEdit.BORDER;		
		pt = getRealPoint(pt);
		
		if (is_dragging && this.glyph.isSimple() && this.editMode == GlyphEdit.EDIT_UPDATE) {				
			if (selectedPoint<0) {
				Debug.printlnErr("Trying to set not existing point!",this);
				return;
			}
			TTFTable_glyfSimple gl = (TTFTable_glyfSimple)glyph;
			gl.updatePoint(selectedPoint, pt, e.isAltDown());							
		}
		else if (is_dragging && glyph.isComposite()) {
			((TTFTable_glyfComposite)glyph).doShift(compositePoint, pt, selectedPoint);
			undoRedo.recordCompositeMove(compositePoint, pt, selectedPoint);
			compositePoint = pt;
		}
		
		if (is_dragging)
			this.requestRepaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		
		Point pt = e.getPoint();
		pt.x -= GlyphEdit.BORDER;
		pt.y -= GlyphEdit.BORDER;		
		pt = getRealPoint(pt);
		
		int prevPoint = selectedPoint;
		boolean matches = getMatchingPoint(pt);
		
		if (matches || prevPoint!=-1 && selectedPoint==-1) {
			this.requestRepaint();
		}
	}
	
	public void mousePressed(MouseEvent e) {
		
		Point pt = e.getPoint();
		pt.x -= GlyphEdit.BORDER;
		pt.y -= GlyphEdit.BORDER;		
		pt = getRealPoint(pt);
		
		/* selectedPoint might have been already set */		
		if (selectedPoint!=-1 || getMatchingPoint(pt)) {
			this.is_dragging = true;
			this.compositePoint = (Point) pt.clone();
			if (e.getButton() != MouseEvent.BUTTON1 && editMode == GlyphEdit.EDIT_ADDPT)
				this.preinsertPoint = this.selectedPoint;
			else if (glyph.isSimple() && editMode == GlyphEdit.EDIT_UPDATE)
				undoRedo.recordSimpleMove(selectedPoint, pt, e.isAltDown());			
		}		
		if (editMode != GlyphEdit.EDIT_UPDATE) {
			try {
				this.editorAddDelAction(e,pt);
			}
			catch (InfoException exc) {
				Util.showExceptionInfo(exc, null, "Error");
			}
			catch (Exception ex) {
				Util.showException(ex, null, "Error");
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		this.is_dragging = false;
		if (selectedPoint!=-1 && this.glyph.isSimple() && editMode == GlyphEdit.EDIT_UPDATE)
			undoRedo.recordSimpleMoveStop(selectedPoint, getRealPoint(e.getPoint()), e.isAltDown());
		this.selectedPoint = -1;
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {
		this.onMouseMove.run();		
	}
	public void mouseExited(MouseEvent e) {}

	public void setEditState(int editState) {
		this.editMode = editState;
		this.preinsertPoint = -1;
		if (editMode == GlyphEdit.EDIT_CONTOUR) {
			startContour = true;
		}
	}
	
	private void onSimpleGlyphEdit(TTFTable_glyfSimple gl, MouseEvent e, Point pt) throws Exception {
		if (editMode == GlyphEdit.EDIT_ADDPT && e.getButton()==MouseEvent.BUTTON1) {
			gl.insertPoint(pt, preinsertPoint, e.isControlDown());
			undoRedo.recordSimpleInsertPoint(pt, preinsertPoint, e.isControlDown());
			this.preinsertPoint = -1;
		}
		else if (editMode == GlyphEdit.EDIT_CONTOUR) {
			gl.addPoint(pt, startContour, e.isControlDown(), -1);
			undoRedo.recordSimpleInsertContour(pt, gl.getPointsCount()-1, startContour, e.isControlDown());
			if (startContour)
				startContour = false;
		}
		else if (editMode == GlyphEdit.EDIT_DELETE && selectedPoint!=-1) {
			int fl = gl.deletePoint(selectedPoint);
			undoRedo.recordSimpleDeletePoint(selectedPoint, pt,
					(fl & TTFTable_glyfSimple.FL_ONCURVE)!=0,
					(fl & TTFTable_glyfSimple.FL_CTSTART)!=0);
			selectedPoint = -1;
		}
		this.requestRepaint();
	}
	
	private void editorAddDelAction(MouseEvent e, Point pt) throws Exception {
		if (glyph.isSimple()) {
			onSimpleGlyphEdit(glyph.getSimple(), e, pt);
		}
		else if (glyph.isComposite()) {
			
			if (editMode == GlyphEdit.EDIT_CONTOUR) {
				return;
			}
			else if (editMode == GlyphEdit.EDIT_ADDPT) {
				String sgid = JOptionPane.showInputDialog(null, "Enter glyph id");
				if (sgid!=null) {
					int gid = 0;
					try {
						gid = Integer.parseInt(sgid);
					}
					catch (Exception ex) {
						throw new InfoException("Invalid id specified!");
					}
					TTFTable_glyfComponent cmpnt = this.glyph.getComposite().addComponentGlyph(gid, pt);
					undoRedo.recordCompositeAddComponent(cmpnt);
				}
			}
			else if (editMode == GlyphEdit.EDIT_DELETE && selectedPoint!=-1) {
				TTFTable_glyfComponent cmpnt = this.glyph.getComposite().deleteComponentGlyph(selectedPoint);
				undoRedo.recordCompositeDelComponent(cmpnt);
			}
			this.requestRepaint();
		}
	}

	public void setDrawOffset(int offX, int offY) {
		if (transform==null)
			return;
		this.ox = offX;
		this.oy = offY;
		this.addTransform( AffineTransform.getTranslateInstance(0, 0) );		
	}

	public void setDrawOffsetX(int offX) {
		if (transform==null)
			return;
		this.ox = offX;
		this.addTransform( AffineTransform.getTranslateInstance(0, 0) );		
	}

	public void setDrawOffsetY(int offY) {
		if (transform==null)
			return;
		this.oy = offY;
		this.addTransform( AffineTransform.getTranslateInstance(0, 0) );		
	}

	public void addDrawOffsetY(int dl) {
		if (transform==null)
			return;
		this.oy += dl;
		this.addTransform( AffineTransform.getTranslateInstance(0, 0) );
	}

	public double getScale() {
		return drawTransform!=null ? drawTransform.getScaleX() : 1.0;
	}
}
