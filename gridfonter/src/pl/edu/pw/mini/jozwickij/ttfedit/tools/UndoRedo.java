package pl.edu.pw.mini.jozwickij.ttfedit.tools;

import java.awt.Point;
import java.util.Vector;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfComponent;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfSimple;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.InfoException;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class UndoRedo {

	private final static int MOVE_ACTION = 1;
	private TTFTable_glyfGeneric glyf = null;
	private int cursor = 0;
	private Vector<UndoRedoAction> actions = new Vector<UndoRedoAction>();
		
	public void recordSimpleInsertPoint(final Point pt, final int pre, final boolean offc) {
		Debug.println("UNDO: recording point insert",this);
		final UndoRedoAction a = new UndoRedoAction(0);
		Runnable u = new Runnable() {
			public void run() {
				glyf.getSimple().deletePoint(pre+1);				
			}
		};
		Runnable r = new Runnable() {
			public void run() {
				try {
					glyf.getSimple().insertPoint(pt, pre, offc);					
				}
				catch (InfoException ex) {
					Util.showExceptionInfo(ex, null, "Exception");
				}
				catch (Exception ex) {
					Util.showException(ex, null, "Exception");
				}			
			}
		};
		a.setUndo(u);
		a.setRedo(r);
		actions.add(cursor++, a);
		this.purgeSince(cursor);
	}

	public void recordSimpleInsertContour(final Point pt, final int pos, final boolean startContour, final boolean offc) {
		Debug.println("UNDO: recording insert to new contour",this);
		final UndoRedoAction a = new UndoRedoAction(0);
		Runnable u = new Runnable() {
			public void run() {
				int fl = glyf.getSimple().deletePoint(pos);
				boolean cst = (fl & TTFTable_glyfSimple.FL_CTSTART)!=0;
				glyf.getSimple().setFixup((startContour|cst) ? TTFTable_glyfSimple.FX_NEWCONTOUR : 0);
			}
		};
		Runnable r = new Runnable() {
			public void run() {
				try {
					glyf.getSimple().setFixup(startContour ? TTFTable_glyfSimple.FX_NEWCONTOUR : 0);
					glyf.getSimple().addPoint(pt, startContour, offc, pos);
				}
				catch (InfoException ex) {
					Util.showExceptionInfo(ex, null, "Exception");
				}
				catch (Exception ex) {
					Util.showException(ex, null, "Exception");
				}
			}
		};
		a.setUndo(u);
		a.setRedo(r);
		actions.add(cursor++, a);
		this.purgeSince(cursor);
	}

	public void recordSimpleDeletePoint(final int sel, final Point pt, final boolean onc, final boolean cst) {
		Debug.println("UNDO: recording delete pt, cst="+cst,this);
		final UndoRedoAction a = new UndoRedoAction(0);
		Runnable r = new Runnable() {
			public void run() {
				glyf.getSimple().deletePoint(sel);
				glyf.getSimple().setFixup(cst ? TTFTable_glyfSimple.FX_NEWCONTOUR : 0);				
			}
		};
		Runnable u = new Runnable() {

			public void run() {
				try {
					glyf.getSimple().setFixup(cst ? TTFTable_glyfSimple.FX_NEWCONTOUR : 0);
					glyf.getSimple().insertPoint(pt, sel-1, !onc);					
				}
				catch (InfoException ex) {
					Util.showExceptionInfo(ex, null, "Exception");
				}
				catch (Exception ex) {
					Util.showException(ex, null, "Exception");
				}
			}
		};
		a.setUndo(u);
		a.setRedo(r);
		actions.add(cursor++, a);
		this.purgeSince(cursor);
	}
	
	public void recordSimpleMove(final int sel, final Point pt, final boolean opt) {
		Debug.println("UNDO: recording move",this);
		final UndoRedoAction a = new UndoRedoAction(MOVE_ACTION, null, null, pt);
		Runnable u = new Runnable() {

			public void run() {
				if (glyf.isSimple()) {
					glyf.getSimple().updatePoint(sel, pt, opt);					
				}				
			}
		};
		a.setUndo(u);
		actions.add(cursor++, a);
		this.purgeSince(cursor);
	}

	public void recordSimpleMoveStop(final int sel, final Point pt, final boolean opt) {
		Debug.println("UNDO: recording move stop",this);
		if (actions.size()>0 && actions.lastElement().causeID == MOVE_ACTION) {
			final UndoRedoAction a = actions.lastElement();
			a.setRedo(new Runnable() {
	
				public void run() {
					if (glyf.isSimple()) {
						glyf.getSimple().updatePoint(sel, pt, opt);
					}					
				}
			});
		}		
	}

	public void recordCompositeAddComponent(final TTFTable_glyfComponent cmpnt) {
		Debug.println("UNDO: recording component add",this);
		Runnable u = new Runnable() {

			public void run() {
				glyf.getComposite().components.remove(cmpnt);				
			}
		};
		Runnable r = new Runnable() {

			public void run() {
				glyf.getComposite().components.add(cmpnt);				
			}
		};
		actions.add(cursor++, new UndoRedoAction(0, u, r));
		this.purgeSince(cursor);
	}

	public void recordCompositeDelComponent(final TTFTable_glyfComponent cmpnt) {
		Debug.println("UNDO: recording del component",this);
		Runnable r = new Runnable() {

			public void run() {
				glyf.getComposite().components.remove(cmpnt);				
			}
		};
		Runnable u = new Runnable() {

			public void run() {
				glyf.getComposite().components.add(cmpnt);				
			}
		};
		actions.add(cursor++,new UndoRedoAction(0, u, r));
		this.purgeSince(cursor);
	}
	
	public void recordCompositeMove(final Point to, final Point from, final int layer) {
		Debug.println("UNDO: recording composite move",this);
		Runnable r = new Runnable() {

			public void run() {
				glyf.getComposite().doShift(to, from, layer);				
			}
		};
		Runnable u = new Runnable() {

			public void run() {
				glyf.getComposite().doShift(from, to, layer);				
			}
		};
		actions.add(cursor++, new UndoRedoAction(MOVE_ACTION+1, u, r));
		this.purgeSince(cursor);
	}
	
	public void loadGlyph(TTFTable_glyfGeneric glf) {
		this.glyf = glf;		
	}

	synchronized public boolean undo() {
		if (cursor>0) {
			Debug.println("UNDO: undo requested",this);
			actions.get(--cursor).undo();
			return true;
		}
		return false;
	}

	synchronized public boolean redo() {
		if (cursor < actions.size() && cursor>=0) {
			Debug.println("REDO: redo requested",this);
			actions.get(cursor++).redo();
			return true;
		}
		return false;
	}

	synchronized public void purge() {
		this.actions.clear();
		this.cursor = 0;		
	}
	
	private void purgeSince(int cur) {
		while (true) {
			if (cur >= actions.size())
				break;
			else
				actions.remove(cur);
		}			
	}	
}
