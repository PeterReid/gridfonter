package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.io.RandomAccessFile;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_post extends TTFTable {
	
	public final static int VER_1_0 = 0x00010000;// for version 1.0
	public final static int VER_2_0 = 0x00020000;// for version 2.0
	public final static int VER_2_5 = 0x00025000;// for version 2.5 (deprecated)
	public final static int VER_3_0 = 0x00030000;// for version 3.0, e.g. MS Calibri from NT 6.0
	
	public int format = VER_3_0;		//int32 
	public int italicAngle = 0;			//Italic angle in counter-clockwise degrees from the vertical. Zero for upright text, negative for text that leans to the right (forward).
	public int underlinePosition = 0;	//This is the suggested distance of the top of the underline from the baseline (negative values indicate below baseline).
										//The PostScript definition of this FontInfo dictionary key (the y coordinate of the center of the stroke) is not used for historical reasons. The value of the PostScript key may be calculated by subtracting half the underlineThickness from the value of this field.
	public int underlineThickness = 0;	//Suggested values for the underline thickness.
	public int isFixedPitch = 0;		//Set to 0 if the font is proportionally spaced, non-zero if the font is not proportionally spaced (i.e. monospaced).
	public int minMemType42 = 0;		//Minimum memory usage when an OpenType font is downloaded.
	public int maxMemType42 = 0;		//Maximum memory usage when an OpenType font is downloaded.
	public int minMemType1 = 0;			//Minimum memory usage when an OpenType font is downloaded as a Type 1 font.
	public int maxMemType1 = 0;			//Maximum memory usage when an OpenType font is downloaded as a Type 1 font.
	public int numberOfGlyphs_v2 = 0;	//ushort number of glyphs, matches MAXP
	public int[] glyphNameIndex_v2=null;//[numberOfGlyphs] 	Ordinal number of this glyph in 'post' string tables. This is not an offset.
	public String[] names_v2 = null;	//[numberNewGlyphs] glyph names with length bytes [variable] (a Pascal string)
	
	@Override public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.format = ttf.readInt();
		this.italicAngle = ttf.readInt();
		this.underlinePosition = ttf.readShort();
		this.underlineThickness = ttf.readShort();
		this.isFixedPitch = ttf.readInt();
		this.minMemType42 = ttf.readInt();
		this.maxMemType42 = ttf.readInt();
		this.minMemType1 = ttf.readInt();
		this.maxMemType1 = ttf.readInt();
		Debug.println("Version of post is="+(format >> 16),this);
		if (format >> 16 == 2) {
			this.numberOfGlyphs_v2 = ttf.readUnsignedShort();
			this.glyphNameIndex_v2 = new int[numberOfGlyphs_v2];
			int max = 0;
			for (int i=0; i < numberOfGlyphs_v2; i++) {
				glyphNameIndex_v2[i] = ttf.readUnsignedShort();
				if (glyphNameIndex_v2[i]>max) {
					max = glyphNameIndex_v2[i];
				}
			}
			this.names_v2 = new String[ (max > 257) ? max-257 : max];
			for (int i=0; i < names_v2.length; i++) {
				int len = ttf.readUnsignedByte();
				byte[] str = new byte[len];
				ttf.readFully(str);
				this.names_v2[i] = new String(str);				
			}
		}
		return true;
	}
	
	private final static String[] names = {
		".notdef", //0
		".null",
		"nonmarkingreturn",
		"space",
		"exclam",
		"quotedbl",
		"numbersign",
		"dollar",
		"percent",
		"ampersand",
		"quotesingle",
		"parenleft",
		"parenright",
		"asterisk",
		"plus",
		"comma",
		"hyphen",
		"period",
		"slash",
		"zero",
		"one",
		"two",
		"three",
		"four",
		"five",
		"six",
		"seven",
		"eight",
		"nine",
		"colon",
		"semicolon",
		"less",
		"equal",
		"greater",
		"question",
		"at",
		"A",
		"B",
		"C",
		"D",
		"E",
		"F",
		"G",
		"H",
		"I",
		"J",
		"K",
		"L",
		"M",
		"N",
		"O",
		"P",
		"Q",
		"R",
		"S",
		"T",
		"U",
		"V",
		"W",
		"X",
		"Y",
		"Z",
		"bracketleft",
		"backslash",
		"bracketright",
		"asciicircum",
		"underscore",
		"grave",
		"a",
		"b",
		"c",
		"d",
		"e",
		"f",
		"g",
		"h",
		"i",
		"j",
		"k",
		"l",
		"m",
		"n",
		"o",
		"p",
		"q",
		"r",
		"s",
		"t",
		"u",
		"v",
		"w",
		"x",
		"y",
		"z",
		"braceleft",
		"bar",
		"braceright",
		"asciitilde",
		"Adieresis",
		"Aring",
		"Ccedilla",
		"Eacute",
		"Ntilde",
		"Odieresis",
		"Udieresis",
		"aacute",
		"agrave",
		"acircumflex",
		"adieresis",
		"atilde",
		"aring",
		"ccedilla",
		"eacute",
		"egrave",
		"ecircumflex",
		"edieresis",
		"iacute",
		"igrave",
		"icircumflex",
		"idieresis",
		"ntilde",
		"oacute",
		"ograve",
		"ocircumflex",
		"odieresis",
		"otilde",
		"uacute",
		"ugrave",
		"ucircumflex",
		"udieresis",
		"dagger",
		"degree",
		"cent",
		"sterling",
		"section",
		"bullet",
		"paragraph",
		"germandbls",
		"registered",
		"copyright",
		"trademark",
		"acute",
		"dieresis",
		"notequal",
		"AE",
		"Oslash",
		"infinity",
		"plusminus",
		"lessequal",
		"greaterequal",
		"yen",
		"mu",
		"partialdiff",
		"summation",
		"product",
		"pi",
		"integral",
		"ordfeminine",
		"ordmasculine",
		"Omega",
		"ae",
		"oslash",
		"questiondown",
		"exclamdown",
		"logicalnot",
		"radical",
		"florin",
		"approxequal",
		"Delta",
		"guillemotleft",
		"guillemotright",
		"ellipsis",
		"nonbreakingspace",
		"Agrave",
		"Atilde",
		"Otilde",
		"OE",
		"oe",
		"endash",
		"emdash",
		"quotedblleft",
		"quotedblright",
		"quoteleft",
		"quoteright",
		"divide",
		"lozenge",
		"ydieresis",
		"Ydieresis",
		"fraction",
		"currency",
		"guilsinglleft",
		"guilsinglright",
		"fi",
		"fl",
		"daggerdbl",
		"periodcentered",
		"quotesinglbase",
		"quotedblbase",
		"perthousand",
		"Acircumflex",
		"Ecircumflex",
		"Aacute",
		"Edieresis",
		"Egrave",
		"Iacute",
		"Icircumflex",
		"Idieresis",
		"Igrave",
		"Oacute",
		"Ocircumflex",
		"apple",
		"Ograve",
		"Uacute",
		"Ucircumflex",
		"Ugrave",
		"dotlessi",
		"circumflex",
		"tilde",
		"macron",
		"breve",
		"dotaccent",
		"ring",
		"cedilla",
		"hungarumlaut",
		"ogonek",
		"caron",
		"Lslash",
		"lslash",
		"Scaron",
		"scaron",
		"Zcaron",
		"zcaron",
		"brokenbar",
		"Eth",
		"eth",
		"Yacute",
		"yacute",
		"Thorn",
		"thorn",
		"minus",
		"multiply",
		"onesuperior",
		"twosuperior",
		"threesuperior",
		"onehalf",
		"onequarter",
		"threequarters",
		"franc",
		"Gbreve",
		"gbreve",
		"Idotaccent",
		"Scedilla",
		"scedilla",
		"Cacute",
		"cacute",
		"Ccaron",
		"ccaron",
		"dcroat",
	};
	
	public String getGlyphName(int i) {
		try {
			if (glyphNameIndex_v2!=null && glyphNameIndex_v2[i] > 257) {
				return names_v2[glyphNameIndex_v2[i]-258];			
			}
			else if (glyphNameIndex_v2!=null) {
				return names[glyphNameIndex_v2[i]];
			}
			else return i+"";
		}
		catch (IndexOutOfBoundsException ex) {
			Debug.printlnErr("Invalid index for glyph name passed to POST function: "+i,this);
			return names[0];
		}
    }
	
	public void insertGlyphName(int id, String name) {
		if (glyphNameIndex_v2==null)
			return;
		this.glyphNameIndex_v2 = Util.makeRoom(glyphNameIndex_v2, id, 1, 0);
		for (int i=0; i < names.length; i++) {
			if (names[i].equals(name)) {
				glyphNameIndex_v2[id] = i;
				return;
			}
		}
		names_v2 = Util.makeRoom(String.class, names_v2, names_v2.length, 1, names[0]);
		int len = names_v2.length-1;
		names_v2[len] = name;
		glyphNameIndex_v2[id] = len+258;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeInt(this.format);
		ttf.writeInt(this.italicAngle);
		ttf.writeShort(this.underlinePosition);
		ttf.writeShort(this.underlineThickness);
		ttf.writeInt(this.isFixedPitch);
		ttf.writeInt(this.minMemType42);
		ttf.writeInt(this.maxMemType42);
		ttf.writeInt(this.minMemType1);
		ttf.writeInt(this.maxMemType1);
		if (format >> 16 == 2) {
			ttf.writeShort(this.numberOfGlyphs_v2 = glyphNameIndex_v2.length);
			for (int i=0; i < numberOfGlyphs_v2; i++) {
				ttf.writeShort(glyphNameIndex_v2[i]);				
			}
			for (int i=0; i < names_v2.length; i++) {
				byte[] b = names_v2[i].getBytes("ASCII");
				ttf.writeByte(b.length);
				ttf.write(b);	
			}
		}
		this.finishWrite(ttf);
		return true;
	}	
}
