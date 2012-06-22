package pl.edu.pw.mini.jozwickij.ttfedit.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Util {
	
	public final static long UINT_MASK = (1L << 32)-1L;
	private final static int straceDepth = 5;
	
	public final static boolean checkOverrideMethod(Class clazz, Class subclazz, String name)
	{
		Method mc = null;
		Method msc = null;
		for (Method m : clazz.getMethods()) {
			if (m.getName().compareTo(name)==0) {
				mc = m;
				break;
			}
		}
		for (Method m : clazz.getMethods()) {
			if (m.getName().compareTo(name)==0) {
				msc = m;
				break;
			}
		}
		if (mc==null || msc==null)
			return false;
		return !mc.equals(msc);
	}

	public static Object describeException(Throwable ex) {
		String s = ex.toString();
		String trace = "\n";
		StackTraceElement[] stack = ex.getStackTrace();
		for (int i=0; i< Math.min(straceDepth, stack.length); i++) {
			trace += stack[i]+ "\n";			
		}
		return s+trace;
	}
	
	public static Properties readConfig(String file) {
		Properties p = new Properties();
		FileInputStream fis = null;
		try {
			 fis = new FileInputStream(file);
		}
		catch (Exception e) { return p; }
		try {
			p.loadFromXML(fis);
		}
		catch (IOException e) {}
		try {
			fis.close();
		}
		catch (IOException e) {}
		return p;
	}
	
	public static void saveConfig(String file, Properties props) {
		FileOutputStream fos = null;
		try {
			 fos = new FileOutputStream(file);
		}
		catch (Exception e) { return; }
		try {
			props.storeToXML(fos,file);
		}
		catch (Exception e) {}
		try {
			fos.close();
		}
		catch (IOException e) {}
	}
	
	public static void showException(Throwable ex, JFrame frame, String caption) {
		JOptionPane.showMessageDialog(	frame,Util.describeException(ex),
										caption,
										JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showExceptionInfo(Throwable ex, JFrame frame, String caption) {
		JOptionPane.showMessageDialog(	frame,ex.getMessage()+"",
										caption,
										JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showInfo(JFrame frame, String caption, String  text) {
		JOptionPane.showMessageDialog(	frame, text, caption, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static JFrame getTopFrame(Component c) {
		JFrame f = null;
		while (c!=null) {
			Component p = c.getParent();
			if (p instanceof JFrame) {
				f = (JFrame)p;
				break;
			}
			else if (p==null) {
				break;
			}
			else {
				c = p;
			}
		}
		return f;
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}
	
	public static File getWindowsFontsDir(String fontName) {
		return new File(System.getenv("WINDIR")+"\\Fonts\\"+fontName).getAbsoluteFile();		
	}
	
	public static double Math_log2(double x) {
		return Math.log10(x)/Math.log10(2.0);
	}
	
	public final static int[] makeRoom(int[] tab, int inject, int blocks, int filler) {
		int[] newTab = null;
		if (inject >= tab.length) {
			newTab = new int[inject+blocks];
			System.arraycopy(tab, 0, newTab, 0, tab.length);
			Arrays.fill(newTab, tab.length, newTab.length, filler);
		}
		else {
			newTab = new int[tab.length+blocks];
			System.arraycopy(tab, 0, newTab, 0, inject);
			System.arraycopy(tab, inject, newTab, inject+blocks, tab.length-inject);
			Arrays.fill(newTab, inject, inject+blocks, filler);
		}
		return newTab;
	}
	
	public final static byte[] makeRoom(byte[] tab, int inject, int blocks, byte filler) {
		byte[] newTab = null;
		if (inject >= tab.length) {
			newTab = new byte[inject+blocks];
			System.arraycopy(tab, 0, newTab, 0, tab.length);
			Arrays.fill(newTab, tab.length, newTab.length, filler);
		}
		else {
			newTab = new byte[tab.length+blocks];
			System.arraycopy(tab, 0, newTab, 0, inject);
			System.arraycopy(tab, inject, newTab, inject+blocks, tab.length-inject);
			Arrays.fill(newTab, inject, inject+blocks, filler);
		}
		return newTab;
	}
	
	@SuppressWarnings("unchecked")
	public final static<T> T[] makeRoom(Class<T> type, T[] tab, int inject, int blocks, T filler) {
		T[] newTab = null;
		if (inject >= tab.length) {
			newTab = (T[]) Array.newInstance(type, inject+blocks);
			System.arraycopy(tab, 0, newTab, 0, tab.length);
			Arrays.fill(newTab, tab.length, newTab.length, filler);
		}
		else {
			newTab = (T[]) Array.newInstance(type, tab.length+blocks);
			System.arraycopy(tab, 0, newTab, 0, inject);
			System.arraycopy(tab, inject, newTab, inject+blocks, tab.length-inject);
			Arrays.fill(newTab, inject, inject+blocks, filler);
		}
		return newTab;
	}
	
	public final static int[] shrink(int[] tab, int delpos, int blocks) {
		int[] newTab = new int[tab.length-blocks];
		System.arraycopy(tab, 0, newTab, 0, delpos);
		System.arraycopy(tab, delpos+blocks, newTab, delpos, tab.length-blocks-delpos);
		return newTab;
	}
	
	public final static byte[] shrink(byte[] tab, int delpos, int blocks) {
		byte[] newTab = new byte[tab.length-blocks];
		System.arraycopy(tab, 0, newTab, 0, delpos);
		System.arraycopy(tab, delpos+blocks, newTab, delpos, tab.length-blocks-delpos);
		return newTab;
	}
	
	@SuppressWarnings("unchecked")
	public final static<T> T[] shrink(Class<T> type, T[] tab, int delpos, int blocks) {
		T[] newTab = (T[]) Array.newInstance(type, tab.length-blocks);
		System.arraycopy(tab, 0, newTab, 0, delpos);
		System.arraycopy(tab, delpos+blocks, newTab, delpos, tab.length-blocks-delpos);
		return newTab;
	}
	
	public static void installKeyListener(Container container, KeyListener listener) {
		container.removeKeyListener(listener);
		container.addKeyListener(listener);
		for (Component c : container.getComponents()) {
			c.addKeyListener(listener);
			if (c instanceof Container)
				installKeyListener((Container) c, listener);
		}
	}
	
	public static void uninstallKeyListener(Container container, KeyListener listener) {
		container.removeKeyListener(listener);
		for (Component c : container.getComponents()) {
			c.addKeyListener(listener);
			if (c instanceof Container)
				uninstallKeyListener((Container) c, listener);
		}
	}
}