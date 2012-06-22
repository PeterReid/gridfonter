package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTObject;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;

public class TTFTable_glyfComponent extends TTObject {
	
	public final static int ARG_1_AND_2_ARE_WORDS	 = 1 << 0; //If this is set, the arguments are words; otherwise, they are bytes.
	public final static int ARGS_ARE_XY_VALUES		 = 1 << 1; //If this is set, the arguments are xy values; otherwise, they are points.
	public final static int ROUND_XY_TO_GRID		 = 1 << 2; //For the xy values if the preceding is true.
	public final static int WE_HAVE_A_SCALE			 = 1 << 3; //This indicates that there is a simple scale for the component. Otherwise, scale = 1.0.
	public final static int RESERVED				 = 1 << 4; //This bit is reserved. Set it to 0.
	public final static int MORE_COMPONENTS			 = 1 <<	5; //Indicates at least one more glyph after this one.
	public final static int WE_HAVE_AN_X_AND_Y_SCALE = 1 <<	6; //The x direction will use a different scale from the y direction.
	public final static int WE_HAVE_A_TWO_BY_TWO 	 = 1 <<	7; //There is a 2 by 2 transformation that will be used to scale the component.
	public final static int WE_HAVE_INSTRUCTIONS	 = 1 <<	8; //Following the last component are instructions for the composite character.
	public final static int USE_MY_METRICS			 = 1 <<	9; //If set, this forces the aw and lsb (and rsb) for the composite to be equal to those from this original glyph. This works for hinted and unhinted characters.
	public final static int OVERLAP_COMPOUND		 = 1 <<	10;//Used by Apple in GX fonts.
	public final static int SCALED_COMPONENT_OFFSET  = 1 <<	11;//Composite designed to have the component offset scaled (designed for Apple rasterizer).
	public final static int UNSCALED_COMPONENT_OFFSET= 1 <<	12;//Composite designed not to have the component offset scaled (designed for the Microsoft TrueType rasterizer).
	public final static double FDIV = 16384.0;
	
	public final static int DFLAGS = WE_HAVE_AN_X_AND_Y_SCALE | ARG_1_AND_2_ARE_WORDS | ARGS_ARE_XY_VALUES;
	
	public int flags = DFLAGS;	//	Component flag
	public int glyphIndex = 0;	//	Glyph index of component
	public int argument1 = 0;	//	X-offset for component or point number; type depends on bits 0 and 1 in component flags
	public int argument2 = 0;	//	Y-offset for component or point number type depends on bits 0 and 1 in component flags
	public int xscale = 1<<14, yscale = 1<<14, scale01 = 0, scale10 = 0;
	
	private Rectangle bbox = null;
	
	private Map<String, TTFTable> ttfTables = null;
	
	@Fields({
			@TField(name="flags",type=Type.USHORT),
			@TField(name="glyphIndex",type=Type.USHORT),
			@TField(name="argument1",type=Type.SHORT),
			@TField(name="argument2",type=Type.SHORT),
			@TField(name="xscale",type=Type.SHORT),
			@TField(name="yscale",type=Type.SHORT),
			@TField(name="scale01",type=Type.SHORT),
			@TField(name="scale10",type=Type.SHORT)
	})
	
	public TTFTable_glyfComponent(int index, Map<String, TTFTable> tables) {
		this.glyphIndex = index;
		this.ttfTables = tables;
	}
	
	public TTFTable_glyfComponent(DataInput ttf, Map<String, TTFTable> tables) throws Exception {
		
		this.flags = ttf.readUnsignedShort();
		this.glyphIndex = ttf.readUnsignedShort();
		if ((flags & ARG_1_AND_2_ARE_WORDS) !=0) {
			this.argument1 = ttf.readShort();
			this.argument2 = ttf.readShort();
		} else {
			this.argument1 = ttf.readByte();
			this.argument2 = ttf.readByte();
		}
		
		if ((flags & WE_HAVE_A_SCALE) !=0) {
			xscale = yscale = ttf.readShort();			
		} 
		else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) !=0) {
			xscale = ttf.readShort();
			yscale = ttf.readShort();			
		}
		else if ((flags & WE_HAVE_A_TWO_BY_TWO) !=0) {
			xscale = ttf.readShort();
			scale01 = ttf.readShort();
			scale10 = ttf.readShort();
			yscale = ttf.readShort();			
		}
		this.ttfTables = tables;
	}
	
	public AffineTransform getTransform() {
		AffineTransform transform = AffineTransform.getTranslateInstance(0,0);		
		transform.concatenate( AffineTransform.getScaleInstance(xscale/FDIV,yscale/FDIV) );
		transform.concatenate( AffineTransform.getTranslateInstance(argument1,argument2) );
		return transform;		
	}

	public void write(RandomAccessFile ttf) throws IOException {
		ttf.writeShort(this.flags);
		ttf.writeShort(this.glyphIndex);
		if ((flags & ARG_1_AND_2_ARE_WORDS) !=0) {
			ttf.writeShort(this.argument1);
			ttf.writeShort(this.argument2);
		} else {
			ttf.writeByte(this.argument1);
			ttf.writeByte(this.argument2);
		}
		
		if ((flags & WE_HAVE_A_SCALE) !=0) {
			ttf.writeShort(xscale);
		} 
		else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) !=0) {
			ttf.writeShort(xscale);
			ttf.writeShort(yscale);
		}
		else if ((flags & WE_HAVE_A_TWO_BY_TWO) !=0) {
			ttf.writeShort(xscale);
			ttf.writeShort(scale01);
			ttf.writeShort(scale10);
			ttf.writeShort(yscale);
		}		
	}
	
	public void doShift(int dx, int dy) {
		this.argument1 += dx;
		this.argument2 += dy;
		this.flags |= ARG_1_AND_2_ARE_WORDS;		
	}
	
	public Rectangle getBoundingBox() {
		if (bbox==null) {
			TTFTable_glyf glyfTab = ((TTFTable_glyf)ttfTables.get(TTFTables.GLYF));
			bbox = glyfTab.glyphs.get(this.glyphIndex).getBoundingBox();
		}
		Rectangle box = (Rectangle) bbox.clone();
		box.x += this.argument1;
		box.y += this.argument2;
		return box;
	}

	public TTFTable_glyfComponent clone() {
		TTFTable_glyfComponent copy = new TTFTable_glyfComponent(glyphIndex, ttfTables);
		copy.argument1 = this.argument1;
		copy.argument2 = this.argument2;
		copy.flags = this.flags;
		copy.scale01 = this.scale01;
		copy.scale10 = this.scale10;
		copy.xscale = this.xscale;
		copy.yscale = this.yscale;
		return copy;
	}
	
	
}
 