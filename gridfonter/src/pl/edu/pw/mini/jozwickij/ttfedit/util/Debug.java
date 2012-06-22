package pl.edu.pw.mini.jozwickij.ttfedit.util;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;



public class Debug {

	public static void print(Object o){
		if (DefaultProperties.DEBUG && DefaultProperties.DEBUG_LEVEL>1)
			System.out.print(o+"");
	}
	
	public static void println(Object o){
		if (DefaultProperties.DEBUG && DefaultProperties.DEBUG_LEVEL>1)
			System.out.println(o+"");
	}
	
	public static void printErr(Object o){
		if (DefaultProperties.DEBUG)
			System.err.print(o+"");
	}
	
	public static void printlnErr(Object o){
		if (DefaultProperties.DEBUG)
			System.err.println(o+"");
	}
	
	public static void print(Object o, Object sender){
		if (DefaultProperties.DEBUG && DefaultProperties.DEBUG_LEVEL>1) {
			if (sender instanceof Class) {
				System.out.print("{sender="+((Class)sender).getName()+"}\n\t"+o+"");
			}
			else {
				System.out.print("{sender="+sender.getClass().getName()+"}\n\t"+o+"");
			}
		}
	}
	
	public static void println(Object o, Object sender){
		if (DefaultProperties.DEBUG && DefaultProperties.DEBUG_LEVEL>1) {
			if (sender instanceof Class) {
				System.out.println("{sender="+((Class)sender).getName()+"}\n\t"+o+"");
			}
			else {
				System.out.println("{sender="+sender.getClass().getName()+"}\n\t"+o+"");
			}
		}
	}
	
	public static void printErr(Object o, Object sender){
		if (DefaultProperties.DEBUG) {
			if (sender instanceof Class) {
				System.err.print("{sender="+((Class)sender).getName()+"}\n\t"+o+"");
			}
			else {
				System.err.print("{sender="+sender.getClass().getName()+"}\n\t"+o+"");
			}
		}
	}
	
	public static void printlnErr(Object o, Object sender){
		if (DefaultProperties.DEBUG) {
			if (sender instanceof Class) {
				System.err.println("{sender="+((Class)sender).getName()+"}\n\t"+o+"");
			}
			else {
				System.err.println("{sender="+sender.getClass().getName()+"}\n\t"+o+"");
			}
		}
	}
}
