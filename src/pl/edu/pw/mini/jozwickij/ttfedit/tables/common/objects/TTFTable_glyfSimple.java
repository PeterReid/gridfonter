package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.io.DataInput;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.hinting.Program;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;
import pl.edu.pw.mini.jozwickij.ttfedit.util.RandomAccessInput;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_glyfSimple extends TTFTable_glyfGeneric {
	
	public final static int ON_CURVE		   	= 1 << 0;	//If set, the point is on the curve; Otherwise, off the curve.
	public final static int X_SHORT_VECTOR	    = 1 << 1;	//If set, the corresponding x-coordinate is 1 byte long; Otherwise, 2 bytes long
	public final static int Y_SHORT_VECTOR     	= 1 << 2;	//If set, the corresponding y-coordinate is 1 byte long; Otherwise, 2 bytes long
	public final static int REPEAT			   	= 1 << 3;	//If set, the next byte specifies the number of additional times this set of flags is to be repeated. In this way, the number of flags listed can be smaller than the number of points in a character.
	public final static int POS_X_SHORT_VECTOR 	= 1 << 4;	//This flag has one of two meanings, depending on how the x-Short Vector flag is set.
															//If the x-Short Vector bit is set, this bit describes the sign of the value, with a value of 1 equalling positive and a zero value negative.
															//If the x-short Vector bit is not set, and this bit is set, then the current x-coordinate is the same as the previous x-coordinate.
															//If the x-short Vector bit is not set, and this bit is not set, the current x-coordinate is a signed 16-bit delta vector. In this case, the delta vector is the change in x
	public final static int POS_Y_SHORT_VECTOR 	= 1 << 5;	//This flag has one of two meanings, depending on how the y-Short Vector flag is set.
															//If the y-Short Vector bit is set, this bit describes the sign of the value, with a value of 1 equalling positive and a zero value negative.
															//If the y-short Vector bit is not set, and this bit is set, then the current y-coordinate is the same as the previous y-coordinate.
															//If the y-short Vector bit is not set, and this bit is not set, the current y-coordinate is a signed 16-bit delta vector. In this case, the delta vector is the change in y
	public final static int RESERVED_6		   	= 1 << 6;	//Reserved 	6 - 7 	Set to zero
	public final static int RESERVED_7		   	= 1 << 7;	//Reserved 	6 - 7 	Set to zero
			
	//////////////////////////////////////////
	public final static int BYTE_MAX_VAL  = 255;
	private final static int ACTION_ADDPT  = 0;
	private final static int ACTION_INSERT = 1;
	private final static int ACTION_DELETE = 2;
	public static final int FL_ONCURVE = 1;
	public static final int FL_CTSTART = 2;
	static public final int FX_NEWCONTOUR = 1;	
	//////////////////////////////////////////
	public int endPtsOfContours[] = null;	// Array of [n] last points of each contour; n is the number of contours.
	public int instructionLength =0;		//	Total number of bytes for instructions.
	public byte[] instructions = null;		//	Array of instructions for each glyph; n is the number of instructions.
	public byte[] flags = null;				// 	Array of flags for each coordinate in outline; n is the number of flags.
	public int[] xCoordinates = null;		//	First coordinates relative to (0,0); others are relative to previous point.
	public int[] yCoordinates = null;		// 	First coordinates relative to (0,0); others are relative to previous point.
	public int[] xs = null;
	public int[] ys = null;
	private Map<Integer,GeneralPath> contours = Collections.synchronizedMap(new HashMap<Integer,GeneralPath>());
	private int fixups = 0;
	
	@Fields(
			{@TField(name="contours",type=Type.OBJ)}
	)

	public TTFTable_glyfSimple(RandomAccessInput ttf, Map<String, TTFTable> tables) throws Exception {
		super(ttf,tables);
		this.endPtsOfContours = new int[this.numberOfContours];
		for (int i=0; i<this.numberOfContours; i++) {
			this.endPtsOfContours[i] = ttf.readUnsignedShort();
		}
		this.instructionLength = ttf.readUnsignedShort();
		this.instructions = new byte[this.instructionLength];
		ttf.readFully(this.instructions);
		
		int points = numberOfContours > 0 ?
				this.endPtsOfContours[this.numberOfContours-1]+1 : 0;
		this.flags = new byte[points];
		
		/*Debug.println("Glyph simple has "+points+" points",this);*/
		
		for (int i=0; i<points; i++) {
			flags[i] = ttf.readByte();
			if ((flags[i] & REPEAT) !=0) {
				int cnt = ttf.readByte();
				if (cnt>=1 && i+cnt<points) {
					Arrays.fill(flags,i+1,i+cnt+1,flags[i]);
				}
				else {
					Debug.println("Glyph flag RLE error. Cnt is "+cnt+" at i="+i,this);
				}
				i+=cnt;
			}
		}
		this.loadPoints(ttf);
		this.countLength(ttf);
	}

	public TTFTable_glyfSimple(Map<String, TTFTable> tables) {
		super(tables); /* empty glyph */
		this.numberOfContours = 0;
		endPtsOfContours = new int[0];
		instructions = new byte[0];
		flags = new byte[0];
		this.xCoordinates = new int[0];
		this.yCoordinates = new int[0];
		xs = new int[0];
		ys = new int[0];
	}
	
	private void loadPoints(DataInput ttf) throws Exception {
		
		int points = flags.length;
		int x = 0;
		this.xCoordinates = new int[points];
		this.xs = new int[points];
		
		for (int i=0; i<points; i++) {
			
			if ((flags[i] & X_SHORT_VECTOR)!=0) {
				if ((flags[i] & POS_X_SHORT_VECTOR)!=0) {
					x += (this.xCoordinates[i] = ttf.readUnsignedByte());
				}
				else {
					x -= (this.xCoordinates[i] = ttf.readUnsignedByte());
				}
			}
			else if ((flags[i] & POS_X_SHORT_VECTOR)==0) {
				x += (this.xCoordinates[i] = ttf.readShort());
			}
			else {
				/* x is the same as previous */
			}
			xs[i] = x;
		}
		
		int y = 0;
		this.yCoordinates = new int[points];
		this.ys = new int[points];
		
		for (int i=0; i<points; i++) {
			
			if ((flags[i] & Y_SHORT_VECTOR) != 0) {
				if ((flags[i] & POS_Y_SHORT_VECTOR)!=0) {
					y += (this.yCoordinates[i] = ttf.readUnsignedByte());
				}
				else {
					y -= (this.yCoordinates[i] = ttf.readUnsignedByte());
				}
			}
			else if ((flags[i] & POS_Y_SHORT_VECTOR)==0) {
				y += (this.yCoordinates[i] = ttf.readShort());
			}
			else {
				/* y is the same as previous */
			}
			ys[i] = y;			
		}		
	}
	
	private GeneralPath buildContour(int c, int s, int cnt, GeneralPath gp) {
		int offset = 0;
		Shape sh = null;
		if (gp==null) {
			gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
		}
				
		while (offset < cnt) {
			int prev  = (offset==0) ? this.endPtsOfContours[c] : s + (offset-1) % cnt;
			int curr  = s + offset % cnt;
			int next1 = s + (offset+1) % cnt;
			int next2 = s + (offset+2) % cnt;
			
			if ( bit(flags[curr],ON_CURVE) ) {
				
				if ( bit(flags[next1],ON_CURVE) ) {
					sh = new Line2D.Float(		xs[curr],ys[curr],
												xs[next1],ys[next1] );
					offset+=1;
				}
				else if ( /*!bit(flags[next1],ON_CURVE) &&*/ bit(flags[next2],ON_CURVE) ) {
					sh = new QuadCurve2D.Float(xs[curr],ys[curr],
												xs[next1],ys[next1],
												xs[next2],ys[next2] );
					offset+=2;
				}
				else /*if ( !bit(flags[next1],ON_CURVE) &&* !bit(flags[next2],ON_CURVE) )*/ {
					sh = new QuadCurve2D.Float(	xs[curr],ys[curr],
												xs[next1],ys[next1],
												mid(xs[next1],xs[next2]), mid(ys[next1],ys[next2]) );
					offset+=2;
				}
			}
			else if ( !bit(flags[curr],ON_CURVE) ) {
				
				if ( !bit(flags[next1],ON_CURVE) ) {
					sh = new QuadCurve2D.Float(	mid(xs[curr],xs[prev]), mid(ys[curr],ys[prev]),
												xs[curr],ys[curr],
												mid(xs[curr],xs[next1]), mid(ys[curr],ys[next1]) );
					offset+=1;
				}
				else {
					sh = new QuadCurve2D.Float(	mid(xs[curr],xs[prev]), mid(ys[curr],ys[prev]),
												xs[curr],ys[curr],
												xs[next1],ys[next1] );
					offset+=1;
				}
			}
			else {
				Debug.printlnErr("Glyph contour segment not handled!!!",this);
				offset++;
				break;
	        }			
			gp.append(sh.getPathIterator(null),true);			
		}
		return gp;
	}
	
	private void buildGlyphContours() {
		
		GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
		if (numberOfContours==0 || endPtsOfContours.length==0) { /* empty glyph */
			contours.put(0,gp);
			return;
		}
		int c = 0, s = 0, cnt = this.endPtsOfContours[c]-s+1;
				
		for (int i=0; i < flags.length; ) {
			
			if (i > this.endPtsOfContours[c]) { /* new contour */
				contours.put(c,gp);
				gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
				s = this.endPtsOfContours[c++]+1;
				cnt = this.endPtsOfContours[c]-s+1;							
			}
			this.buildContour(c,s,cnt,gp);
			i = this.endPtsOfContours[c]+1;
		}
		contours.put(c,gp);
	}
	
	public Collection<GeneralPath> getContours() {
		if (contours.size()==0) {
			this.buildGlyphContours();			
		}
		return this.contours.values();
	}
	
	private int index2Contour(int i) {
		int len = this.endPtsOfContours.length;
		if (i>= xs.length)
			return len;
		for (int c=1; c < len; c++) {
			if (i > this.endPtsOfContours[c-1] && i <= this.endPtsOfContours[c] ) {				
				return c;
			}
		}
		return endPtsOfContours.length>0 ? 0 : -1;
	}
	
	private int getContourEnd(int ptIndex) {
		return this.endPtsOfContours[index2Contour(ptIndex)];
	}

	public void updatePoint(int idx, Point pt, boolean option) {
		int dx = pt.x - xs[idx];
		int dy = pt.y - ys[idx];
		xs[idx] = pt.x;
		ys[idx] = pt.y;
		
		/* fixing current and next point */
		this.xCoordinates[idx] +=  dx;
		this.yCoordinates[idx] +=  dy;
		if (idx+1 <= getContourEnd(idx) && !option) {
			this.xCoordinates[idx+1] -=  dx;
			this.yCoordinates[idx+1] -=  dy;
		}
		if (option) {
			for (int i=idx+1; i < xs.length; i++) {
				xs[i] += dx;
				ys[i] += dy;
			}
		}
		/* rebuilding contour */
		int c = this.index2Contour(idx);
		int s = c>0 ? this.endPtsOfContours[c-1]+1 : 0;
		int cnt = this.endPtsOfContours[c]-s+1;
		contours.put(c, this.buildContour(c,s,cnt,null));
	}

	@Override
	public int getPointsCount() {
		return xs.length;
	}
	
	@Override
	public void fixBoundingBox() {
		
		if (this.xs.length < 1) return;
		xMin = xMax = xs[0];
		yMin = yMax = ys[0];
		
		for (GeneralPath gp : getContours()) {
			Rectangle box = gp.getBounds();
			if (box.x < xMin)
				xMin = box.x;
			if (box.getMaxX() > xMax)
				xMax = box.x + box.width;
			if (box.y < yMin)
				yMin = box.y;
			if (box.getMaxY() > yMax)
				yMax = box.y + box.height;
		}		
	}
	
	private void fixFlag(int i) {
		int currFlags = this.flags[i] & ON_CURVE;
		
		if (i==0) {
			this.xCoordinates[0] = this.xs[0];
			this.yCoordinates[0] = this.ys[0];			
		}
		else {
			this.xCoordinates[i] = this.xs[i] - this.xs[i-1];
			this.yCoordinates[i] = this.ys[i] - this.ys[i-1];
		}
		
		if (this.xCoordinates[i]==0) {
			currFlags |= POS_X_SHORT_VECTOR; /* no change in x */			
		}
		else if (Math.abs(this.xCoordinates[i]) <= BYTE_MAX_VAL) {
			currFlags |= X_SHORT_VECTOR;
			if (this.xCoordinates[i] > 0) {
				currFlags |= POS_X_SHORT_VECTOR;
			}
			else {
				this.xCoordinates[i]*=-1;
			}
		}
		/* else { } */
		
		if (this.yCoordinates[i]==0) {
			currFlags |= POS_Y_SHORT_VECTOR; /* no change in y */			
		}
		else if (Math.abs(this.yCoordinates[i]) <= BYTE_MAX_VAL) {
			currFlags |= Y_SHORT_VECTOR;
			if (this.yCoordinates[i] > 0) {
				currFlags |= POS_Y_SHORT_VECTOR;				
			}
			else {
				this.yCoordinates[i]*=-1;
			}
		}
		/* else { } */
		
		if (i>1 && currFlags==flags[i-1]) {
			currFlags |= REPEAT;
			flags[i-1] = (byte)(currFlags | REPEAT);			
		}
		this.flags[i] = (byte)(currFlags);
	}	

	private void writeFlags(RandomAccessFile ttf) throws Exception {
		for (int i=0; i < flags.length; i++) {
			fixFlag(i);
		}
		int off = 0;
		while (off < flags.length) {				
			ttf.writeByte(flags[off]);
			if ((flags[off] & REPEAT)!=0) {
				int fw = off+1;
				while (fw<flags.length && flags[fw]==flags[off]) {
					fw++;
				}
				ttf.writeByte((fw-off-1));
				off=fw;
			}
			else {
				off++;
			}
		}		
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		if ((this.numberOfContours=endPtsOfContours.length) > 0 ) {			
			ttf.writeShort(this.numberOfContours);
			this.fixBoundingBox();
			ttf.writeShort(this.xMin);
			ttf.writeShort(this.yMin);
			ttf.writeShort(this.xMax);
			ttf.writeShort(this.yMax);
			for (int i=0; i<this.numberOfContours; i++) {
				ttf.writeShort( this.endPtsOfContours[i] );
			}
			if (DefaultProperties.PRESERVE_TTF_ASM) {
				ttf.writeShort( this.instructionLength=this.instructions.length );
				ttf.write( this.instructions );
			}
			else {
				this.instructions = Program.getInstructions();
				ttf.writeShort(this.instructionLength=this.instructions.length);
				ttf.write(this.instructions);
			}
			
			writeFlags(ttf);
			
			for (int i=0; i<flags.length; i++) {
				if ((flags[i] & X_SHORT_VECTOR)!=0) {
					ttf.writeByte(this.xCoordinates[i] & BYTE_MAX_VAL);
				}
				else if ((flags[i] & POS_X_SHORT_VECTOR)==0) {
					ttf.writeShort(this.xCoordinates[i]);
				}
			}
			for (int i=0; i<flags.length; i++) {
				if ((flags[i] & Y_SHORT_VECTOR)!=0) {
					ttf.writeByte(this.yCoordinates[i] & BYTE_MAX_VAL);
				}
				else if ((flags[i] & POS_Y_SHORT_VECTOR)==0) {
					ttf.writeShort(this.yCoordinates[i]);
				}
			}
			
		}
		this.finishWrite(ttf);
		return true;
	}

	@Override
	public boolean isEmpty() {
		return xs.length < 1;
	}
	
	private int getContourStart(int i) {
		return (i>=1) ? (endPtsOfContours[i-1]+1) : 0;			   
	}
	
	private int getContourCount(int i) {
		return (i>=1) ? (endPtsOfContours[i] - endPtsOfContours[i-1]) : 
			   (i>=0) ? endPtsOfContours[0]+1 : 0;
	}
	
	private void checkInvalidContour(int s, Point pt) throws Exception {
		if (s == 1) {
			int dx = pt.x - xs[0];
			int dy = pt.y - ys[0];
			if ( (dx < 0 && dy < 0) || (dx < 0 && dy==0) || (dx==0 && dy<0) ) {
				throw new InfoException("First points of contour cannot be laid anticlockwise");
			}
		}
	}
	
	public void addPoint(Point pt, boolean startsContour, boolean ofc, int s) throws Exception {
		if (s<0)
			s = xs.length;
		if ((this.fixups & FX_NEWCONTOUR)!=0) {
			fixups = 0;
			startsContour = true;
		}
		int ctr = this.index2Contour(startsContour ? s : s-1) ;
		checkInvalidContour(s, pt);		
		Debug.println("Adding point "+pt+" with param cst="+startsContour+" at pos="+s+" ct="+ctr,this);

		if (startsContour)
			this.endPtsOfContours = Util.makeRoom(endPtsOfContours, ctr, 1, s);			
		else
			this.endPtsOfContours[ctr] = s;
		
		this.flags = Util.makeRoom(flags, s, 1, (byte)(ofc ? 0 : ON_CURVE) );
		this.xCoordinates = Util.makeRoom(xCoordinates, s, 1, 0);
		this.yCoordinates = Util.makeRoom(yCoordinates, s, 1, 0);
		this.xs = Util.makeRoom(xs, s, 1, pt.x);
		this.ys = Util.makeRoom(ys, s, 1, pt.y);
		xCoordinates[s] = (s > 1) ? xs[s] - xs[s-1] : xs[s];
		yCoordinates[s] = (s > 1) ? ys[s] - ys[s-1] : ys[s];
		int cnt = this.getContourCount(ctr);
		fixContourFlags(ctr, s, s-cnt+1, cnt, ACTION_ADDPT);
		contours.put(ctr, buildContour(ctr, s-cnt+1, cnt, null));
	}

	public void insertPoint(Point pt, int s, boolean ofc) throws Exception {
		if (s++<0)
			throw new InfoException("Select first (by right click) the preceding point of insertion!");
		Debug.println("Requested insertion of: "+pt+" at pos="+s,this);
		if ((this.fixups & FX_NEWCONTOUR)!=0) {
			fixups = 0;
			addPoint(pt, true, ofc, s);
			return;
		}
		this.flags = Util.makeRoom(flags, s, 1, (byte)(ofc ? 0 : ON_CURVE));
		this.xCoordinates = Util.makeRoom(xCoordinates, s, 1, 0);
		this.yCoordinates = Util.makeRoom(yCoordinates, s, 1, 0);
		this.xs = Util.makeRoom(xs, s, 1, 0);
		this.ys = Util.makeRoom(ys, s, 1, 0);
		xs[s] = pt.x;
		ys[s] = pt.y;
		int i = this.index2Contour(s) + (xs.length==s+1 ? 1 : 0);
				
		for (int k=i; k < this.endPtsOfContours.length; k++) {
			this.endPtsOfContours[k]++;
		}
		int cnt = this.getContourCount(i);
		fixContourFlags(i, s, endPtsOfContours[i]-cnt+1, cnt, ACTION_INSERT);
		contours.put(i, buildContour(i, endPtsOfContours[i]-cnt+1, cnt, null));
	}
	
	public int deletePoint(int sel) {
		Debug.println("Delete point "+sel+" [x="+xs[sel]+",y="+ys[sel]+"]",this);
		boolean oncurve = (this.flags[sel] & ON_CURVE) !=0;
		int c = this.index2Contour(sel);
		int cnt = getContourCount(c)-1;
		this.flags = Util.shrink(flags, sel, 1);
		this.xCoordinates = Util.shrink(xCoordinates, sel, 1);
		this.yCoordinates = Util.shrink(yCoordinates, sel, 1);
		this.xs = Util.shrink(xs, sel, 1);
		this.ys = Util.shrink(ys, sel, 1);
		
		for (int i=c; i < this.endPtsOfContours.length; i++) {
			this.endPtsOfContours[i]--;
		}
		
		if (cnt<=0) {
			this.endPtsOfContours = Util.shrink(endPtsOfContours, c, 1);
			contours.remove(c);
			Debug.println("Contours rebuild",this);
			for (int i=c+1; i < contours.size()-1; i++) {
				contours.put(i-1, contours.get(i));
			}
			return (oncurve ? FL_ONCURVE : 0) | FL_CTSTART;			
		}
		else {
			int s = this.getContourStart(c);
			cnt = endPtsOfContours[c] - s + 1;
			Debug.println("Rebuilding contour "+c+": "+s+"->"+this.endPtsOfContours[c]+", length="+cnt,this);
			fixContourFlags(c, sel, s, cnt, ACTION_DELETE);
			contours.put(c, buildContour(c, s, cnt, null));
		}
		return oncurve ? FL_ONCURVE : 0;
	}
	
	private void fixContourFlags(int c, int sadd, int start, int cnt, int action) {
		return;
	}

	@Override
	public TTFTable_glyfSimple clone() {
		TTFTable_glyfSimple copy = new TTFTable_glyfSimple(ttftables);
		for (int i=0; i < this.contours.size(); i++) {
			copy.contours.put( i, (GeneralPath) contours.get(i).clone() );			
		}
		copy.endPtsOfContours = this.endPtsOfContours.clone();
		copy.flags = this.flags.clone();
		copy.instructionLength = this.instructionLength;
		copy.instructions = this.instructions.clone();
		copy.numberOfContours = this.numberOfContours;
		copy.xCoordinates = this.xCoordinates.clone();
		copy.xs = this.xs.clone();
		copy.yCoordinates = this.yCoordinates.clone();
		copy.ys = this.ys.clone();
		this.cloneBounds(copy);
		return copy;
	}

	public void setFixup(int fx_flags) {
		this.fixups  = fx_flags;		
	}	
}
