package pl.edu.pw.mini.jozwickij.ttfedit.tools;

public interface ScannerCallback {
	
	public final static int LOAD = 0;
	public final static int SAVE = 1;
	public void onScan(String filename, boolean start);
	public void onException(String filename, int mode, String info);
	public void onEnd(boolean aborted);

}
