package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Vector;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.hinting.Program;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_maxp;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;
import pl.edu.pw.mini.jozwickij.ttfedit.util.RandomAccessInput;

public class TTFTable_glyfComposite extends TTFTable_glyfGeneric {
	
	public Vector<TTFTable_glyfComponent> components = new Vector<TTFTable_glyfComponent>();
	public byte[] instructions = null;
		
	@Fields(
			{@TField(name="components",type=Type.OBJ)}
	)
	
	public TTFTable_glyfComposite(Map<String, TTFTable> tables) {
		super(tables);
		this.numberOfContours = -1;
		this.instructions = new byte[0];
	}
	
	public TTFTable_glyfComposite(RandomAccessInput ttf, Vector<TTFTable_glyfGeneric> glyphs, Map<String, TTFTable> tables) throws Exception {
		super(ttf,tables);
		TTFTable_glyfComponent component = null;
		do {
			component = new TTFTable_glyfComponent(ttf, tables);
			if ((component.flags & TTFTable_glyfComponent.ARGS_ARE_XY_VALUES)==0) {
				Debug.printlnErr("Composite glyph has strange arguments",this);
			}
			else {
				components.add(component);
			}
		}
		while ((component.flags & TTFTable_glyfComponent.MORE_COMPONENTS)!=0);
		
		if ((component.flags & TTFTable_glyfComponent.WE_HAVE_INSTRUCTIONS)!=0) {
			this.instructions = new byte[ttf.readShort()];
			ttf.readFully(this.instructions);
		}
		this.countLength(ttf);
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeShort(this.numberOfContours);
		this.fixBoundingBox();
		ttf.writeShort(this.xMin);
		ttf.writeShort(this.yMin);
		ttf.writeShort(this.xMax);
		ttf.writeShort(this.yMax);
		
		for (int i=0; i < components.size(); i++) {
			TTFTable_glyfComponent gl = components.get(i);
			if (i < components.size()-1)
				gl.flags |= TTFTable_glyfComponent.MORE_COMPONENTS;
			else
				gl.flags &= ~TTFTable_glyfComponent.MORE_COMPONENTS;
			gl.write(ttf);
		}
		if (!DefaultProperties.PRESERVE_TTF_ASM) {
			this.instructions = Program.getInstructions();
		}
		ttf.writeShort(this.instructions.length);
		ttf.write( this.instructions );
		this.finishWrite(ttf);
		return true;
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		for (TTFTable_glyfComponent comp : components) {
			TTFTable_glyf glyfTab = (TTFTable_glyf) ttftables.get(TTFTables.GLYF);
			empty &= glyfTab.glyphs.get(comp.glyphIndex).isEmpty();
		}
		return empty;
	}
	
	@Override
	public void fixBoundingBox() {
		Rectangle iBox;
		this.xMin = Integer.MAX_VALUE;
		this.yMin = Integer.MAX_VALUE;
		this.xMax = Integer.MIN_VALUE;
		this.yMax = Integer.MIN_VALUE;
		
		for (TTFTable_glyfComponent comp : components) {
			iBox = comp.getBoundingBox();
			if (iBox.x < xMin)
				xMin = iBox.x;
			if (iBox.y < yMin)
				yMin = iBox.y;
			if (iBox.getMaxX() > xMax)
				xMax = iBox.x + iBox.width;
			if (iBox.getMaxY() > yMax)
				yMax = iBox.y + iBox.height;
		}
	}
	
	public void doShift(Point from, Point to, int layer) {
		
		if (layer < 0 || layer > components.size()) {
			throw new RuntimeException("Invalid layer index to move: "+layer);
		}
		Rectangle iBox = null;
		this.xMin = Integer.MAX_VALUE;
		this.yMin = Integer.MAX_VALUE;
		this.xMax = Integer.MIN_VALUE;
		this.yMax = Integer.MIN_VALUE;
		int k = 0;
						
		for (TTFTable_glyfComponent comp : components) {
			iBox = comp.getBoundingBox();
			if (iBox.contains(from)) {
				if (k == layer) {
					comp.doShift(to.x-from.x, to.y-from.y);
					iBox.x += to.x-from.x;
					iBox.y += to.y-from.y;					
				}							
			}
			if (iBox.x < xMin)
				xMin = iBox.x;
			if (iBox.y < yMin)
				yMin = iBox.y;
			if (iBox.getMaxX() > xMax)
				xMax = iBox.x + iBox.width;
			if (iBox.getMaxY() > yMax)
				yMax = iBox.y + iBox.height;
			k++;
		}		
	}

	public TTFTable_glyfComponent deleteComponentGlyph(int i) {
		TTFTable_glyfComponent cmpnt = this.components.get(i);
		this.components.remove(i);
		return cmpnt;
	}
	
	public TTFTable_glyfComponent addComponentGlyph(int gid, Point pt) throws Exception {
		if (gid==this.getIndex()) {
			throw new InfoException("Composite glyph cannot have itself as component");
		}
		TTFTable_glyf glyfTab = (TTFTable_glyf) ttftables.get(TTFTables.GLYF);
		int maxd = ((TTFTable_maxp) ttftables.get(TTFTables.MAXP)).maxComponentDepth;
		try {
			TTFTable_glyfGeneric gl = glyfTab.glyphs.get(gid);
			checkMaliciousFont(glyfTab, gl, this.getIndex(), maxd);
		}
		catch (ArrayIndexOutOfBoundsException e) {			
			throw new InfoException("No existing glyph with index "+gid);
		}
		TTFTable_glyfComponent gcomp = new TTFTable_glyfComponent(gid, ttftables);
		Rectangle bbox = gcomp.getBoundingBox();
		gcomp.doShift(pt.x-bbox.x, pt.y-bbox.y);
		this.components.add(gcomp);
		return gcomp;
	}

	private void checkMaliciousFont(TTFTable_glyf glyfTab, TTFTable_glyfGeneric gl, int gid, int maxd) throws InfoException {
		if (maxd < 0)
			throw new InfoException("Composite glyph cannot have recursively nested glyph as component");
		if (gl.isComposite()) {
			for (TTFTable_glyfComponent gcomp : gl.getComposite().components) {
				if (gcomp.glyphIndex==gid)
					throw new InfoException("Composite glyph cannot have itself nested as component");
				else {
					TTFTable_glyfGeneric glchild = null;
					glchild = glyfTab.glyphs.get(gcomp.glyphIndex);
					checkMaliciousFont(glyfTab, glchild, gid, maxd-1);					
				}
			}
		}
		else if (gl.getIndex()==gid) {
			throw new InfoException("Composite glyph cannot have invalid nested glyph as component");
		}
	}

	@Override
	public TTFTable_glyfGeneric clone() {
		TTFTable_glyfComposite copy = new TTFTable_glyfComposite(ttftables);
		this.cloneBounds(copy);
		for (TTFTable_glyfComponent cmp : this.components) {
			copy.components.add(cmp.clone());
		}
		copy.instructions = this.instructions.clone();
		copy.numberOfContours = this.numberOfContours;
		return copy;
	}
	
	
}
