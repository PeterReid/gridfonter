import java.awt.Point;
import java.util.List;
import java.util.Vector;

import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_cmap;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_nameRecord;


public class GridFont {
	public static void main(String[] args) throws Exception {
		TTFont f = new TTFont("src/template/VeraMono.ttf", "r");
		
		TTFTable_glyf glyf = (TTFTable_glyf)f.getTable("glyf");
		Vector<TTFTable_glyfGeneric> vec = new Vector<TTFTable_glyfGeneric>();
		for (int i = 0; i < glyf.glyphs.size(); i++) {
			TTFTable_glyfSimple g = new TTFTable_glyfSimple(f.getTablesMap());
			
			Grid grid = new Grid(3,7);
			
			/*grid.connect(new Grid.XY(0,0), new Grid.XY(1,0));
			grid.connect(new Grid.XY(1,0), new Grid.XY(2,1));
			grid.connect(new Grid.XY(2,1), new Grid.XY(2,2));
			grid.connect(new Grid.XY(2,2), new Grid.XY(2,3));
			grid.connect(new Grid.XY(2,3), new Grid.XY(1,2));
			grid.connect(new Grid.XY(1,2), new Grid.XY(1,1));
			grid.connect(new Grid.XY(1,1), new Grid.XY(0,1));
			grid.connect(new Grid.XY(0,1), new Grid.XY(0,0));*/

			grid.connect(new Grid.XY(0,0), new Grid.XY(0,2));
			grid.connect(new Grid.XY(2,0), new Grid.XY(2,2));
			grid.connect(new Grid.XY(0,0), new Grid.XY(2,0));
			//grid.connect(new Grid.XY(0,2), new Grid.XY(2,2));
			grid.connect(new Grid.XY(0,0), new Grid.XY(1,1));
			grid.connect(new Grid.XY(0,2), new Grid.XY(1,1));
			grid.connect(new Grid.XY(2,2), new Grid.XY(1,1));
			grid.connect(new Grid.XY(2,0), new Grid.XY(1,1));
			
			
			//grid.connect(new Grid.XY(0,3), new Grid.XY(1,4));
			//grid.connect(new Grid.XY(1,4), new Grid.XY(2,3));
			//grid.connect(new Grid.XY(2,3), new Grid.XY(1,2));
			//grid.connect(new Grid.XY(1,2), new Grid.XY(0,3));
			//grid.connect(new Grid.XY(2,3), new Grid.XY(2,2));
			
			for (List<Point> ps: grid.trace()) {
				boolean first = true;
				for (Point p: ps) {
					g.addPoint(p, first, false, -1);
					first = false;
				}
			}
			/*
			g.addPoint(new Point(0,0), true, false, -1);
			g.addPoint(new Point(600,0), false, false, -1);
			g.addPoint(new Point(0,600), false, false, -1);
			
			g.addPoint(new Point(700,100), true, false, -1);
			g.addPoint(new Point(700,700), false, false, -1);
			g.addPoint(new Point(100,700), false, false, -1);*/
			vec.add(g);
		}
		glyf.glyphs = vec;
		/*
		for(TTFTable_glyfGeneric g: glyf.glyphs) {
			TTFTable_glyfSimple gs = g.getSimple();
			if (gs==null) continue;
			
			//gs.addPoint(new Point(0,0), true, false, -1);
			//gs.addPoint(new Point(60,0), false, false, -1);
			//gs.addPoint(new Point(0,60), false, false, -1);
			
		}*/
		
		TTFTable_cmap cmap = (TTFTable_cmap)f.getTable("cmap");
		
		f.save("out.ttf");
	}
}
