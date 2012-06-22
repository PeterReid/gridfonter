package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Vector;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_head;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.RandomAccessInput;

public class TTFTable_glyfGeneric extends TTFTable implements Cloneable {
	
	public int numberOfContours = 0; //	If the number of contours is positive or zero, it is a single glyph;
	 								 // If the number of contours is -1, the glyph is compound
	public int xMin = 0;
	public int yMin = 0;
	public int xMax = 0;
	public int yMax = 0;
	
	protected Map<String, TTFTable> ttftables = null;
	
	@Fields(
			{@TField(name="numberOfContours",type=Type.SHORT),
			@TField(name="xMin",type=Type.SHORT),
			@TField(name="yMin",type=Type.SHORT),
			@TField(name="xMax",type=Type.SHORT),
			@TField(name="yMax",type=Type.SHORT)}
	)
		
	protected TTFTable_glyfGeneric(RandomAccessInput ttf, Map<String, TTFTable> tables) throws Exception {
		this.my_offset = (int) ttf.getFilePointer();
		this.numberOfContours = ttf.readShort();
		this.xMin = ttf.readShort();
		this.yMin = ttf.readShort();
		this.xMax = ttf.readShort();
		this.yMax = ttf.readShort();
		this.ttftables = tables;
	}
	
	public void cloneBounds(TTFTable_glyfGeneric dest) {
		dest.xMin = this.xMin;
		dest.xMax = this.xMax;
		dest.yMin = this.yMin;
		dest.yMax = this.yMax;
	}
	
	protected TTFTable_glyfGeneric(Map<String, TTFTable> tables) {
		this.ttftables = tables;
	}
	
	public int getIndex() {
		TTFTable_glyf glyfTab = (TTFTable_glyf) this.ttftables.get(TTFTables.GLYF);
		return glyfTab.glyphs.indexOf(this);
	}
	
	public static void readGlyph(RandomAccessInput ttf, Vector<TTFTable_glyfGeneric> glyphs, Map<String, TTFTable> tables, boolean empty) throws Exception {
		if (empty) {
			glyphs.add( new TTFTable_glyfSimple(tables) );
			return;
		}
		long fp = ttf.getFilePointer();
		int nOfContours = ttf.readShort();
		ttf.seek(fp);
		
		/*Debug.printlnErr("Glyph has "+nOfContours+" contours, starts at "+fp,TTFTable_glyfGeneric.class);*/
		if (nOfContours>=0) {
			glyphs.add( new TTFTable_glyfSimple(ttf, tables) );
		}
		else if (nOfContours==-1){
			glyphs.add( new TTFTable_glyfComposite(ttf, glyphs, tables) );
		}
		else {
			Debug.printlnErr("Glyph has invalid contour's number",TTFTable_glyfGeneric.class);
		}
	}	
	
	public int getEmSize() {
		TTFTable_head head = (TTFTable_head) ttftables.get(TTFTables.HEAD);
		return head.unitsPerEm;		
	}
	
	public int getOverflowWidth() {
		return (xMin<0) ? xMin : 0;
	}
	
	public int getOverflowHeigth() {
		return (yMin<0) ? yMin : 0;
	}
	
	public int getHeight() {
		return yMax - yMin;
	}
	
	public int getWidth() {
		return xMax - xMin;
	}
	
	public boolean isEmpty() { 
		throw new RuntimeException("GenericGlyph cannot be empty or non-empty");
	}
	
	public void fixBoundingBox() {
		throw new RuntimeException("GenericGlyph cannot have bounding box fixed");
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(xMin, yMin, getWidth(), getHeight());		
	}
	
	public AffineTransform getViewTransform(int width, int height) {
		AffineTransform at = AffineTransform.getTranslateInstance(0,0);
		double a = (double)Math.min(width,height);
		double sc = a / this.getEmSize();
		at.scale(sc,-sc);
		at.translate(0,-this.getEmSize());
		at.translate(-this.getOverflowWidth(), -this.getOverflowHeigth());
		return at;
	}
	
	public Map<String, TTFTable> getFontTables() {
		return this.ttftables;
	}
	
	public @Override TTFTable_glyfGeneric clone() { 
		return null;		
	}
	
	public int getPointsCount() {
		return 0;
	}
	
	protected void countLength(RandomAccessInput ttf) {
		this.my_length = (int) (ttf.getFilePointer() - this.my_offset);		
	}

	public int getTabLength() {
		return this.my_length;
	}

	@Override
	protected TTFTable prepareWrite(RandomAccessFile ttf) throws Exception {
		this.my_offset = (int)ttf.getFilePointer();
		return this;
	}
	
	@Override
	protected TTFTable finishWrite(RandomAccessFile ttf) throws Exception {
		this.my_length = (int)(ttf.getFilePointer()-this.my_offset);
		return this;
	}
	
	public boolean isSimple() {
		return this instanceof TTFTable_glyfSimple;
	}
	
	public boolean isComposite() {
		return this instanceof TTFTable_glyfComposite;
	}
	
	public TTFTable_glyfComposite getComposite() {
		if (this instanceof TTFTable_glyfComposite)
			return (TTFTable_glyfComposite)this;
		else
			return null;
	}
	
	public TTFTable_glyfSimple getSimple() {
		if (this instanceof TTFTable_glyfSimple)
			return (TTFTable_glyfSimple)this;
		else
			return null;
	}
}
