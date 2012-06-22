package pl.edu.pw.mini.jozwickij.ttfedit.tools;

import java.awt.Point;

public class UndoRedoAction {
	private Runnable undo = null;
	private Runnable redo = null;
	public int causeID = 0;
	public Point pt = null;
	public Point pt_from = null;
	public Point pt_to = null;
		
	public UndoRedoAction(int cause) {
		this.causeID = cause;
	}
	
	public UndoRedoAction(int cause, Runnable u, Runnable r) {
		this.causeID = cause;
		this.undo = u;
		this.redo = r;
	}
	
	public UndoRedoAction(int cause, Runnable u, Runnable r, Point p) {
		this.causeID = cause;
		this.undo = u;
		this.redo = r;
		this.pt = p;
	}
	
	public void undo() {
		if (undo!=null)
			undo.run();
	}
	public void redo() {
		if (redo!=null)
			redo.run();
	}
	
	public void setUndo(Runnable u) {
		this.undo = u;
	}
	public void setRedo(Runnable r) {
		this.redo = r;
	}
}
