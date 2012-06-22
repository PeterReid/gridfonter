package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

public class TTFTable_cmapMap {
	
	public final static int SHIFT_NONE = 0;
	public static final int SHIFT_FIX_YMIN = 1;
	public final static int SHIFT_SECOND_BY_HALF_FIRST_DOWN = 2;
	public final static int SHIFT_SECOND_BY_HALF_FIRST_UP = 4;	
		
	public char ch;		// character 
	public int  flags;	// composition shift flags
	public char c1;		// char 1 of complex glyph
	public char c2;		// char 2 of complex glyph
	public String name = ".notdef";
			
	public TTFTable_cmapMap(char uni_char, int _flags, char j, char k) {
		this.ch = uni_char;
		this.flags  = _flags;
		this.c1 = j;
		this.c2 = k;			
	}
	
	public TTFTable_cmapMap(char uni_char, int _flags, char j, char k, String _name) {
		this.ch = uni_char;
		this.flags  = _flags;
		this.c1 = j;
		this.c2 = k;
		this.name = _name;
	}	
}