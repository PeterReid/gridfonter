package pl.edu.pw.mini.jozwickij.ttfedit.tools;

import java.io.File;

import javax.swing.JPanel;

import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class Scanner {
	
	private static Thread th = null;
	private static JPanel fr = new JPanel();
	private static boolean canRun = false;
	private static ScannerCallback cback = null;
	
	private static void _scan(String path, ScannerCallback cb) {
		canRun = true;
		cback = cb;
		File f = new File(path);
		File[] fonts = f.listFiles(new TTFilter());
		for (File file : fonts) {
			TTFont ttf = null;
			try {
				if (!canRun) {
					if (ttf!=null)
						ttf.destroy();
					cb.onEnd(true);
					return;
				}
				cb.onScan(file.getAbsolutePath(), true);
				ttf = new TTFont(file.getAbsolutePath(), TTFont.MODE_READ);
				for (TTFTable tab : ttf.getTablesMap().values()) {
					tab.getView(fr, null);
					fr.removeAll();
					if (!canRun) {
						ttf.destroy();
						cb.onEnd(true);
						return;
					}
				}
			}
			catch (Exception e) {
				cb.onException(file.getAbsolutePath(), ScannerCallback.LOAD, Util.describeException(e)+"");
				if (!canRun) {
					if (ttf!=null)
						ttf.destroy();
					cb.onEnd(true);
					return;
				}
				continue;
			}
			try {
				ttf.save("test.ttf");
				if (!canRun) {
					if (ttf!=null)
						ttf.destroy();
					cb.onEnd(true);
					return;
				}
			}
			catch (Exception e) {
				cb.onException(file.getAbsolutePath(), ScannerCallback.SAVE, Util.describeException(e)+"");
				if (!canRun) {
					if (ttf!=null)
						ttf.destroy();
					cb.onEnd(true);
					return;
				}
				continue;
			}
			finally {
				if (ttf!=null)
					ttf.destroy();
			}
			cb.onScan(file.getAbsolutePath(), false);
		}
		cb.onEnd(false);
	}
	
	synchronized public static void scan(final String path, final ScannerCallback cb) {
		if (th==null || !th.isAlive()) {
			th = new Thread() {
				public void run() {
					_scan(path, cb);
				}
			};
			th.start();
		}
	}

	public static void cancel() {
		if (!canRun && th!=null && th.isAlive()) {
			th.interrupt();
			if (cback!=null)
				cback.onEnd(true);
		}
		canRun = false;		
	}

	public static boolean isRunning() {
		return th!=null && th.isAlive();
	}
}
