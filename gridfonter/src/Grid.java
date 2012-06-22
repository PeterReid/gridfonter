import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.ListSelectionEvent;


public class Grid {

	final Segment goesTo[][][]; //[x][y][direction]
	final int w;
	final int h;
	final XY[] pts;

	@SuppressWarnings("unchecked")
	public Grid(int w, int h) {
		this.w = w;
		this.h = h;

		goesTo = new Segment[w][h][8];

		pts = new XY[w*h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				pts[x+y*w] = new XY(x,y);
			}
		}
	}

	void connect(XY from, XY to) {
		Segment fromTo = new Segment(from, to);
		Segment toFrom = new Segment(to, from);

		if (goesTo[from.x][from.y][fromTo.direction] != null) {
			throw new IllegalArgumentException("Already connected");
		}
		if (goesTo[to.x][to.y][toFrom.direction] != null) {
			throw new IllegalArgumentException("Already connected");
		}

		goesTo[from.x][from.y][fromTo.direction] = fromTo;
		goesTo[to.x][to.y][toFrom.direction] = toFrom;
	}

	static class XY implements Comparable<XY> {
		final int x;
		final int y;

		public XY(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public int compareTo(XY o) {
			if (x != o.x) return x - o.x;
			return y - o.y;
		}
		
		public String toString() { 
			return "(" + x + "," + y + ")";
		}
	}

	static class Segment implements Comparable<Segment> {
		final XY from;
		final XY to;
		final int direction;

		static final int[][] directionIndex = {
			{ 1, 2, 3 },
			{ 0,-1, 4 },
			{ 7, 6, 5 }
		};

		static int dimIdx(int from, int to) {
			if (from < to) return 0;
			if (from > to) return 2;
			return 1;
		}

		public Segment(XY from, XY to) {
			if (from.equals(to)) {
				throw new IllegalArgumentException("from same as to");
			}

			this.from = from;
			this.to = to;
			this.direction = directionIndex[dimIdx(from.y, to.y)][dimIdx(from.x, to.x)];
		}

		public int compareTo(Segment arg0) {
			int cmp = from.compareTo(arg0.from);
			if (cmp!= 0) return cmp;

			return to.compareTo(arg0.to);
		}
		
		public String toString() {
			return from + "->" + to + " {" + direction + "}";
		}
	}

	// We're walking along the right-hand edge of a curve. Where is our next step?
	Segment segmentAfter(Segment s) {
		int initialDir = s.direction;

		Segment[] goingsOut = goesTo[s.to.x][s.to.y];

		for (int i = 0; i < 8; i++) {
			int dirOut = (initialDir + 5 + i) % 8;
			if (goingsOut[dirOut] != null) {
				return goingsOut[dirOut];
			}
		}
		assert false : "Got stranded";
		return null;
	}

	/* Line joins are mitered.
	 * 
	 * 
	 *   Forgive the stretching. The diagonal length (X) is the same as the side length (other X, WHOLE)
	 *   
	 *   +--------+
	 *   | /\     |      |                |
	 *   |/  \    |      |                |
	 *   |    \X  |      |                |
	 *   |     \  |X     | LARGE          |
	 *   |      \ |      |      |         | WHOLE
	 *   |       \|      |      |         |
	 *   |       /| |           | HALF    |
	 *   |      / | | SMALL     |         |
	 *   +--------+  
	 * 
	 */
	static final int WHOLE = 100; // stroke thickness
	static final int LARGE = (int)Math.round(WHOLE / Math.sqrt(2));
	static final int SMALL = WHOLE - LARGE;
	static final int HALF = WHOLE / 2;

	/* The i'th index holds the points to miter a change in direction by i steps counterclockwise,
	 * starting from going to the right (+x) along the stroke's right edge.
	 */
	static final XY[][] fromOrtho = {
		{  },
		{ new XY(LARGE, 0) },
		{ new XY(WHOLE, 0) },
		{ new XY(WHOLE, 0), new XY(WHOLE, LARGE) },
		{ new XY(WHOLE, 0), new XY(WHOLE, WHOLE) },
		{ new XY(-LARGE, 0) },
		{ new XY(0, 0) },
		{ new XY(SMALL, 0) },
	};
	
	/* The i'th index holds the points to miter a change in direction by i steps counterclockwise,
	 * starting from going up and to the right (+x,+y) along the stroke's bottom/right edge.
	 */
	static final XY[][] fromDiag = {
		{  },
		{ new XY(WHOLE, SMALL) },
		{ new XY(WHOLE, SMALL), new XY(WHOLE, LARGE) },
		{ new XY(WHOLE, SMALL), new XY(WHOLE, WHOLE) },
		{ new XY(WHOLE, SMALL), new XY(SMALL, WHOLE) },
		{ new XY(0, -LARGE) },
		{ new XY(HALF, SMALL-HALF) },
		{ new XY(LARGE, 0) }
	};

	static final int GRID_SIZE = 300;

	XY rotate(XY a, int rotations) {
		switch(rotations) {
		case 0:
			return a;
		case 1:
			return new XY(WHOLE - a.y, a.x);
		case 2:
			return new XY(WHOLE - a.x, WHOLE - a.y);
		case 3:
			return new XY(a.y, WHOLE - a.x);
		default:
			assert false;
			return null;
		}
	}

	// First points of contour cannot be laid anticlockwise, apparently. See TTFTable_glyfSimple's checkInvalidContour
	List<Point> makeInitialClockwise(ArrayList<Point> pts) {
		int start = 0;
		for (; start < pts.size(); start++) {
			Point from = pts.get(start);
			Point to = pts.get((start+1)%8);
			int dx = to.x - from.x;
			int dy = to.y - from.y;
			if ( (dx < 0 && dy < 0) || (dx < 0 && dy==0) || (dx==0 && dy<0) ) {
				// it is anticlockwise
				continue;
			}
			break;
		}
		if (start==pts.size()) {
			throw new IllegalArgumentException("Can't make non-anticlockwise");
		}
		
		ArrayList<Point> result = new ArrayList<Point>();
		for (int i = start; i < pts.size(); i++) {
			result.add(pts.get(i));
		}
		for (int i = 0; i < start; i++) {
			result.add(pts.get(i));
		}
		
		return result;
	}
	
	void appendTurn(List<Point> pts, Segment in, Segment out) {
		boolean incomingOrtho = in.direction % 2 == 0;
		int deltaDir = (out.direction - in.direction + 8) % 8;
		XY[][] corners = incomingOrtho ? fromOrtho : fromDiag;
		XY[] corner = corners[deltaDir];
		int rotations = in.direction / 2;

		int x = in.to.x * GRID_SIZE;
		int y = in.to.y * GRID_SIZE;
		for (XY delta: corner) {
			XY deltaRotated = rotate(delta, rotations);
			pts.add(new Point(x + deltaRotated.x, y + deltaRotated.y));
		}
	}

	List<List<Point>> trace() {
		List<List<Point>> traces = new ArrayList<List<Point>>();


		Set<Segment> traced = new TreeSet<Segment>();

		for (XY start: pts) {
			for (Segment seg0: goesTo[start.x][start.y]) {
				if (seg0 == null) continue;

				if (traced.contains(seg0)) continue;

				ArrayList<Point> trace = new ArrayList<Point>();

				Segment seg = seg0;
				do {
					traced.add(seg);
					Segment next = segmentAfter(seg);
					appendTurn(trace, seg, next);
					seg = next;
				} while (!seg.equals(seg0));

				traces.add(makeInitialClockwise(trace));
			}
		}

		
		return traces;
	}

	public static void main(String[] args) {

		Grid grid = new Grid(3,7);
		grid.connect(new Grid.XY(1,0), new Grid.XY(2,1));

		grid.trace();
	}
}
