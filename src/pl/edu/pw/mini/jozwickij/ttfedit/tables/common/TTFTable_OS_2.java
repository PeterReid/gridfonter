package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComponent;

import pl.edu.pw.mini.jozwickij.ttfedit.gui.vcontrols.VControl;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;

public class TTFTable_OS_2 extends TTFTable {

	public final static int VERSION_0_MS = 0x0000;
	public final static int VERSION_1_MS = 0x0001;
	public final static int VERSION_2_MS = 0x0002;
	public final static int VERSION_3_MS = 0x0003;
	
	/* Apple made mistake in Visual Width description http://developer.apple.com/fonts/TTRefMan/RM06/Chap6OS2.html
	 * Font used in OS X comply with MS spec : http://www.microsoft.com/typography/otspec/os2.htm */
	
	/* Visual Width */
	public final static int VW_ULTRA_LIGHT 	= 100;
	public final static int VW_EXTRA_LIGHT 	= 200;
	public final static int VW_LIGHT 		= 300;
	public final static int VW_SEMI_LIGHT 	= 400;
	public final static int VW_MEDIUM 		= 500;
	public final static int VW_SEMI_BOLD 	= 600;
	public final static int VW_BOLD 		= 700;
	public final static int VW_EXTRA_BOLD	= 800;
	public final static int VW_ULTRA_BOLD	= 900;
	
	/* Width Class */
	public final static int W_ULTRA_CONDENSED	= 1;
	public final static int W_EXTRA_CONDENSED 	= 2;
	public final static int W_CONDENSED			= 3;
	public final static int W_SEMI_CONDENSED 	= 4;
	public final static int W_MEDIUM 			= 5;
	public final static int W_SEMI_EXPANDED 	= 6;
	public final static int W_EXPANDED 			= 7;
	public final static int W_EXTRA_EXPANDED	= 8;
	public final static int W_ULTRA_EXPANDED	= 9;
	
	/* Legal right for embedding */
	public final static int FS_ALL				= 0x0000;
	public final static int FS_NONE				= 0x0002;
	public final static int FS_PREVIEW_PRINT	= 0x0004;
	public final static int FS_TEMP_EDIT		= 0x0008;
	public final static int FS_NO_SUBSET		= 0x0100;
	public final static int FS_BITMAP_ONLY		= 0x0200;
	
	/* Font characteristics */
	public final static byte FC_NO_CLASS			= 0;
	public final static byte FC_OLDSTYLE_SERIFS		= 1;
	public final static byte FC_TRANSITIONAL_SERIFS = 2;
	public final static byte FC_MODERN_SERIFS		= 3;
	public final static byte FC_CLARENDON_SERIFS	= 4;
	public final static byte FC_SLAB_SERIFS			= 5;
	public final static byte FC_FREEFORM_SERIFS		= 7;
	public final static byte FC_SANS_SERIFS			= 8;
	public final static byte FC_ORNAMENTALS			= 9;
	public final static byte FC_SCRIPTS				= 10;
	public final static byte FC_SYMBOLIC			= 12;
	
	public final static byte FSC_NO_SUBCLASS 		= 0;
	public final static byte FSC_MISCELLANEOUS		= 15;
	
	public final static byte FSC_1_ROUNDED_LEGIBLE	= 1;
	public final static byte FSC_1_GERALDE			= 2;
	public final static byte FSC_1_VENETIAN			= 3;
	public final static byte FSC_1_MODIFIED_VENETIAN= 4;
	public final static byte FSC_1_DUTCH_MODERN		= 5;
	public final static byte FSC_1_DUTCH_TRADITIONAL= 6;
	public final static byte FSC_1_CONTEMPORARY		= 7;
	public final static byte FSC_1_CALLIGRAPHICS	= 8;
	
	public final static byte FSC_2_DIRECT_LINE		= 1;
	public final static byte FSC_2_SCRIPT			= 2;
	
	public final static byte FSC_3_ITALIAN			= 1;
	public final static byte FSC_3_SCRIPT			= 2;
	
	public final static byte FSC_4_CLARENDON		= 1;
	public final static byte FSC_4_MODERN			= 2;
	public final static byte FSC_4_TRADITIONAL		= 3;
	public final static byte FSC_4_NEWSPAPAER		= 4;
	public final static byte FSC_4_STUB_SERIF		= 5;
	public final static byte FSC_4_MONOTONE			= 6;
	public final static byte FSC_4_TYPEWRITER		= 7;
	
	public final static byte FSC_5_MONOTONE			= 1;
	public final static byte FSC_5_HUMANIST			= 2;
	public final static byte FSC_5_GEOMETRIC		= 3;
	public final static byte FSC_5_SWISS			= 4;
	public final static byte FSC_5_TYPEWRITER		= 5;
	
	public final static byte FSC_7_MODERN			= 1;
	
	public final static byte FSC_8_IBM_NEOGROTESQUE_GOTHIC	= 1;
	public final static byte FSC_8_HUMANIST					= 2;
	public final static byte FSC_8_LOWX_ROUND_GEOM			= 3;
	public final static byte FSC_8_HIGHX_ROUND_GEOM			= 4;
	public final static byte FSC_8_NEOGROTESQUE_GOTHIC		= 5;
	public final static byte FSC_8_MOD_NEOGROTESQUE_GOTHIC	= 6;
	public final static byte FSC_8_TYPEWRITER_GOTHIC		= 9;
	public final static byte FSC_8_MATRIX					= 10;
	
	public final static byte FSC_9_ENGRAVER			= 1;
	public final static byte FSC_9_BLACK_LETTER		= 2;
	public final static byte FSC_9_DECORATIVE		= 3;
	public final static byte FSC_9_3D				= 4;
	
	public final static byte FSC_10_UNCIAL			= 1;
	public final static byte FSC_10_BRUSH_JOINED	= 2;
	public final static byte FSC_10_FORMAL_JOINED	= 3;
	public final static byte FSC_10_MONOTONE_JOINED	= 4;
	public final static byte FSC_10_CALLIGRAPHIC	= 5;
	public final static byte FSC_10_BRUSH_UNJOINED	= 6;
	public final static byte FSC_10_FORMAL_UNJOINED	= 7;
	public final static byte FSC_10_MONOTONE_UNJOINED=8;
	
	public final static byte FSC_12_MIXED_SERIF				= 3;
	public final static byte FSC_12_OLDSTYLE_SERIF			= 6;
	public final static byte FSC_12_NEOGROTESQUE_SANS_SERIF	= 7;
	
	/* Panose subclasses */
	public final static byte PAN_1_FAMILYTYPE_ANY			= 0;
	public final static byte PAN_1_FAMILYTYPE_NO_FIT		= 1;
	public final static byte PAN_1_FAMILYTYPE_TEXT_DISPLAY	= 2;
	public final static byte PAN_1_FAMILYTYPE_SCRIPT		= 3;
	public final static byte PAN_1_FAMILYTYPE_DECORATIVE	= 4;
	public final static byte PAN_1_FAMILYTYPE_PICTORIAL		= 5;
	
	public final static byte PAN_2_SERIFSTYLE_ANY				= 0;
	public final static byte PAN_2_SERIFSTYLE_NO_FIT			= 1;
	public final static byte PAN_2_SERIFSTYLE_COVE				= 2;
	public final static byte PAN_2_SERIFSTYLE_OBTUSE_COVE		= 3;
	public final static byte PAN_2_SERIFSTYLE_SQUARE_COVE		= 4;
	public final static byte PAN_2_SERIFSTYLE_OBTUSE_SQUARE_COVE= 5;
	public final static byte PAN_2_SERIFSTYLE_SQUARE			= 6;
	public final static byte PAN_2_SERIFSTYLE_THIN				= 7;
	public final static byte PAN_2_SERIFSTYLE_BONE				= 8;
	public final static byte PAN_2_SERIFSTYLE_EXAGGERATED		= 9;
	public final static byte PAN_2_SERIFSTYLE_TRIANGLE			= 10;
	public final static byte PAN_2_SERIFSTYLE_NORMAL_SANS		= 11;
	public final static byte PAN_2_SERIFSTYLE_OBTUSE_SANS		= 12;
	public final static byte PAN_2_SERIFSTYLE_PERP_SANS			= 13;
	public final static byte PAN_2_SERIFSTYLE_FLARED			= 14;
	public final static byte PAN_2_SERIFSTYLE_ROUNDED			= 15;
	
	public final static byte PAN_3_WEIGHT_ANY				= 0;
	public final static byte PAN_3_WEIGHT_NO_FIT			= 1;
	public final static byte PAN_3_WEIGHT_VERY_LIGHT		= 2;
	public final static byte PAN_3_WEIGHT_LIGHT				= 3;
	public final static byte PAN_3_WEIGHT_THIN				= 4;
	public final static byte PAN_3_WEIGHT_BOOK				= 5;
	public final static byte PAN_3_WEIGHT_MEDIUM			= 6;
	public final static byte PAN_3_WEIGHT_DEMI				= 7;
	public final static byte PAN_3_WEIGHT_BOLD				= 8;
	public final static byte PAN_3_WEIGHT_HEAVY				= 9;
	public final static byte PAN_3_WEIGHT_BLACK				= 10;
	public final static byte PAN_3_WEIGHT_NORD				= 11;
	
	public final static byte PAN_4_PROPORTION_ANY			= 0;
	public final static byte PAN_4_PROPORTION_NO_FIT		= 1;
	public final static byte PAN_4_PROPORTION_OLD_STYLE		= 2;
	public final static byte PAN_4_PROPORTION_MODERN		= 3;
	public final static byte PAN_4_PROPORTION_EVEN_WIDTH	= 4;
	public final static byte PAN_4_PROPORTION_EXPANDED		= 5;
	public final static byte PAN_4_PROPORTION_CONDENSED		= 6;
	public final static byte PAN_4_PROPORTION_VERY_EXPANDED = 7;
	public final static byte PAN_4_PROPORTION_VERY_CONDENSED= 8;
	public final static byte PAN_4_PROPORTION_MONOSPACED	= 9;
	
	public final static byte PAN_5_CONTRAST_ANY				= 0;
	public final static byte PAN_5_CONTRAST_NO_FIT			= 1;
	public final static byte PAN_5_CONTRAST_NONE			= 2;
	public final static byte PAN_5_CONTRAST_VERY_LOW		= 3;
	public final static byte PAN_5_CONTRAST_LOW				= 4;
	public final static byte PAN_5_CONTRAST_MEDIUM_LOW		= 5;
	public final static byte PAN_5_CONTRAST_MEDIUM			= 6;
	public final static byte PAN_5_CONTRAST_MEDIUM_HIGH		= 7;
	public final static byte PAN_5_CONTRAST_HIGH			= 8;
	public final static byte PAN_5_CONTRAST_VERY_HIGH		= 9;
	
	public final static byte PAN_6_STROKEVAR_ANY					= 0;
	public final static byte PAN_6_STROKEVAR_NO_FIT					= 1;
	public final static byte PAN_6_STROKEVAR_GRADUAL_DIAGONAL		= 2;
	public final static byte PAN_6_STROKEVAR_GRADUAL_TRANSITIONAL	= 3;
	public final static byte PAN_6_STROKEVAR_GRADUAL_VERTICAL		= 4;
	public final static byte PAN_6_STROKEVAR_GRADUAL_HORIZONTAL		= 5;
	public final static byte PAN_6_STROKEVAR_RAPID_VERTICAL			= 6;
	public final static byte PAN_6_STROKEVAR_RAPID_HORIZONTAL		= 7;
	public final static byte PAN_6_STROKEVAR_INSTANT_VERTICAL		= 8;
	
	public final static byte PAN_7_ARMSTYLE_ANY							= 0;
	public final static byte PAN_7_ARMSTYLE_NO_FIT						= 1;
	public final static byte PAN_7_ARMSTYLE_STRAIGHT_HORIZONTAL			= 2;
	public final static byte PAN_7_ARMSTYLE_STRAIGHT_WEDGE				= 3;
	public final static byte PAN_7_ARMSTYLE_STRAIGHT_VERTICAL			= 4;
	public final static byte PAN_7_ARMSTYLE_STRAIGHT_SINGLE_SERIF		= 5;
	public final static byte PAN_7_ARMSTYLE_STRAIGHT_DOUBLE_SERIF		= 6;
	public final static byte PAN_7_ARMSTYLE_NONSTRAIGHT_HORIZONTAL		= 7;
	public final static byte PAN_7_ARMSTYLE_NONSTRAIGHT_WEDGE			= 8;
	public final static byte PAN_7_ARMSTYLE_NONSTRAIGHT_VERTICAL		= 9;
	public final static byte PAN_7_ARMSTYLE_NONSTRAIGHT_SINGLE_SERIF	= 10;
	public final static byte PAN_7_ARMSTYLE_NONSTRAIGHT_DOUBLE_SERIF	= 11;
	
	public final static byte PAN_8_LETTERFORM_ANY				= 0;
	public final static byte PAN_8_LETTERFORM_NO_FIT			= 1;
	public final static byte PAN_8_LETTERFORM_NORMAL_CONTACT	= 2;
	public final static byte PAN_8_LETTERFORM_NORMAL_WEIGHTED	= 3;
	public final static byte PAN_8_LETTERFORM_NORMAL_BOXED		= 4;
	public final static byte PAN_8_LETTERFORM_NORMAL_FLATTENED	= 5;
	public final static byte PAN_8_LETTERFORM_NORMAL_ROUNDED	= 6;
	public final static byte PAN_8_LETTERFORM_NORMAL_OFF_CENTER	= 7;
	public final static byte PAN_8_LETTERFORM_NORMAL_SQUARE		= 8;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_CONTACT	= 9;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_WEIGHTED	= 10;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_BOXED		= 11;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_FLATTENED	= 12;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_ROUNDED	= 13;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_OFF_CENTER= 14;
	public final static byte PAN_8_LETTERFORM_OBLIQUE_SQUARE	= 15;
	
	public final static byte PAN_9_MIDLINE_ANY					= 0;
	public final static byte PAN_9_MIDLINE_NO_FIT				= 1;
	public final static byte PAN_9_MIDLINE_STANDARD_TRIMMED		= 2;
	public final static byte PAN_9_MIDLINE_STANDARD_POINTED		= 3;
	public final static byte PAN_9_MIDLINE_STANDARD_SERIFIED	= 4;
	public final static byte PAN_9_MIDLINE_HIGH_TRIMMED			= 5;
	public final static byte PAN_9_MIDLINE_HIGH_POINTED			= 6;
	public final static byte PAN_9_MIDLINE_HIGH_SERIFIED		= 7;
	public final static byte PAN_9_MIDLINE_CONSTANT_TRIMMED		= 8;
	public final static byte PAN_9_MIDLINE_CONSTANT_POINTED		= 9;
	public final static byte PAN_9_MIDLINE_CONSTANT_SERIFIED	= 10;
	public final static byte PAN_9_MIDLINE_LOW_TRIMMED			= 11;
	public final static byte PAN_9_MIDLINE_LOW_POINTED			= 12;
	public final static byte PAN_9_MIDLINE_LOW_SERIFIED			= 13;
	
	public final static byte PAN_10_XHEIGHT_ANY					= 0;
	public final static byte PAN_10_XHEIGHT_NO_FIT				= 1;
	public final static byte PAN_10_XHEIGHT_CONSTANT_SMALL		= 2;
	public final static byte PAN_10_XHEIGHT_CONSTANT_STANDARD	= 3;
	public final static byte PAN_10_XHEIGHT_CONSTANT_LARGE		= 4;
	public final static byte PAN_10_XHEIGHT_DUCKING_SMALL		= 5;
	public final static byte PAN_10_XHEIGHT_DUCKING_STANDARD	= 6;
	public final static byte PAN_10_XHEIGHT_DUCKING_LARGE		= 7;
	
	/* Modifier on selection */
	public final static byte FSEL_ITALIC 		= 1 << 0;
	public final static byte FSEL_UNDERSCORE 	= 1 << 1;
	public final static byte FSEL_NEGATIVE 		= 1 << 2;
	public final static byte FSEL_OUTLINED 		= 1 << 3;
	public final static byte FSEL_STRIKEOUT		= 1 << 4;
	public final static byte FSEL_BOLD 			= 1 << 5;
	public final static byte FSEL_NORMAL 		= 1 << 6;
	
	/* Unicode ranges */
	public final static int UR1_BASIC_LATIN					= 1 << 0;
	public final static int UR1_LATIN1_SUPPLEMENT			= 1 << 1;
	public final static int UR1_LATIN_EXT_A					= 1 << 2;
	public final static int UR1_LATIN_EXT_B					= 1 << 3;
	public final static int UR1_IPA_EXT						= 1 << 4;
	public final static int UR1_SPACING_MODIFIER_LETTERS	= 1 << 5;
	public final static int UR1_COMBINING_DIACRITICAL_MARKS	= 1 << 6;
	public final static int UR1_GREEK_COPTIC				= 1 << 7;
	public final static int UR1_UNI_SUB8					= 1 << 8;
	public final static int UR1_CYRILLIC					= 1 << 9;
	public final static int UR1_ARMENIAN					= 1 << 10;
	public final static int UR1_HEBREW						= 1 << 11;
	public final static int UR1_UNI_SUB12					= 1 << 12;
	public final static int UR1_ARABIC						= 1 << 13;
	public final static int UR1_UNI_SUB14					= 1 << 14;
	public final static int UR1_DEVANAGARI					= 1 << 15;
	public final static int UR1_BENGALI						= 1 << 16;
	public final static int UR1_GURMUKHI					= 1 << 17;
	public final static int UR1_GUJARATI					= 1 << 18;
	public final static int UR1_ORIYA						= 1 << 19;
	public final static int UR1_TAMIL						= 1 << 20;
	public final static int UR1_TELUGU						= 1 << 21;
	public final static int UR1_KANNADA						= 1 << 22;
	public final static int UR1_MALAYALAM					= 1 << 23;
	public final static int UR1_THAI						= 1 << 24;
	public final static int UR1_LAO							= 1 << 25;
	public final static int UR1_GEORGIAN					= 1 << 26;
	public final static int UR1_UNI_SUB27					= 1 << 27;
	public final static int UR1_HANGUL_JAMO					= 1 << 28;
	public final static int UR1_LATIN_EXT_ADDITIONAL		= 1 << 29;
	public final static int UR1_GREEK_EXT					= 1 << 30;
	public final static long UR1_GENERAL_PUNCTUATION		= 1L << 31;
	
	public final static int UR2_SUP_SUB									= 1 << 0;
	public final static int UR2_CURRENCY								= 1 << 1;
	public final static int UR2_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS = 1 << 2;
	public final static int UR2_LETTERLIKE_SYMBOLS						= 1 << 3;
	public final static int UR2_NUMBER_FORMS							= 1 << 4;
	public final static int UR2_ARROWS									= 1 << 5;
	public final static int UR2_MATHEMATICAL_OPERATORS_AND_SYMBOLS		= 1 << 6;
	public final static int UR2_MISC_TECHNICAL							= 1 << 7;
	public final static int UR2_CONTROL_PICTURES						= 1 << 8;
	public final static int UR2_OPTICAL_CHARS_RECOGNITION				= 1 << 9;
	public final static int UR2_ENCLOSED_ALPHANUMERICS					= 1 << 10;
	public final static int UR2_BOX_DRAWING								= 1 << 11;
	public final static int UR2_BLOCK_ELEMENTS							= 1 << 12;
	public final static int UR2_GEOMETRIC_SHAPES						= 1 << 13;
	public final static int UR2_MISC_SYMBOLS							= 1 << 14;
	public final static int UR2_DINGBATS								= 1 << 15;
	public final static int UR2_CJK_SYMBOLS_AND_PUNCTUATION				= 1 << 16;
	public final static int UR2_HIRAGANA								= 1 << 17;
	public final static int UR2_KATAKANA								= 1 << 18;
	public final static int UR2_BOPOMOFO								= 1 << 19;
	public final static int UR2_HANGUL_COMPATIBILITY_JAMO				= 1 << 20;
	public final static int UR2_UNI_SUB53								= 1 << 21;
	public final static int UR2_ENCLOSED_CJK_LETTERS_AND_MONTHS			= 1 << 22;
	public final static int UR2_CJK_COMPAT								= 1 << 23;
	public final static int UR2_HANGUL_SYLLABES							= 1 << 24;
	public final static int UR2_NON_PLANE_ZERO_STAR						= 1 << 25;
	public final static int UR2_UNI_SUB58								= 1 << 26;
	public final static int UR2_CJK_KANGXI_KANBUN_AND_IDEOGRAMS			= 1 << 27;
	public final static int UR2_PRIVATE60								= 1 << 28;
	public final static int UR2_CJK_COMPAT_IDEOGRAMS					= 1 << 29;
	public final static int UR2_ALPHABETIC_PRESENTATION_FORMS			= 1 << 30;
	public final static long UR2_ARABIC_PRESENTATION_FORMS_A			= 1L << 31;
	
	public final static int UR3_COMBINING_HALF_MARKS			= 1 << 0;
	public final static int UR3_CJK_COMPAT_FORMS				= 1 << 1;
	public final static int UR3_SMALL_FORMS_VARIANTS			= 1 << 2;
	public final static int UR3_ARABIC_PRESENTATION_FORMS_B		= 1 << 3;
	public final static int UR3_HALFWIDTH_FULLWIDTH_FORMS		= 1 << 4;
	public final static int UR3_SPECIALS						= 1 << 5;
	public final static int UR3_TIBETAN							= 1 << 6;
	public final static int UR3_SYRIAC							= 1 << 7;
	public final static int UR3_THAANA							= 1 << 8;
	public final static int UR3_SINHALA							= 1 << 9;
	public final static int UR3_MYANMAR							= 1 << 10;
	public final static int UR3_ETHIOPIC						= 1 << 11;
	public final static int UR3_CHEROKEE						= 1 << 12;
	public final static int UR3_CANADIAN_ABORIGINAL_SYLLABICS	= 1 << 13;
	public final static int UR3_OGHAM							= 1 << 14;
	public final static int UR3_RUNIC							= 1 << 15;
	public final static int UR3_KHMER							= 1 << 16;
	public final static int UR3_MONGOLIAN						= 1 << 17;
	public final static int UR3_BRAILLE_PATTERNS				= 1 << 18;
	public final static int UR3_YI								= 1 << 19;
	public final static int UR3_TAGALOG_HANUNOO_BUHID_TAGBANHWA	= 1 << 20;
	public final static int UR3_OLD_ITALIC						= 1 << 21;
	public final static int UR3_GOTHIC							= 1 << 22;
	public final static int UR3_DESERET							= 1 << 23;
	public final static int UR3_MUSICAL_SYMBOLS					= 1 << 24;
	public final static int UR3_MATHEMATICAL_ALPHANUMERIC		= 1 << 25;
	public final static int UR3_PRIVATE90						= 1 << 26;
	public final static int UR3_VARIATION_SELECTORS				= 1 << 27;
	public final static int UR3_TAGS							= 1 << 28;
	public final static int UR3_UNI_SUB93_AND_ABOVE				= 1 << 29;
	
	public final static int UR4_UNI_RESERVERD					= 0 << 0;
	public final static int MS_CP_LATIN2 						= 1 << 1;
	
	//////////////////////////////////////////
	public int version;						// 	table version number (set to 0)  	 
	public int xAvgCharWidth;				// 	average weighted advance width of lower case letters and space 	 
	public int usWeightClass;				// 	visual weight (degree of blackness or thickness) of stroke in glyphs 	 
	public int usWidthClass;				// 	relative change from the normal aspect ratio (width to height ratio) as specified by a font designer for the glyphs in the font 	
	public int fsType;						// 	characteristics and properties of this font (set undefined bits to zero) 	 
	public int ySubscriptXSize;				// 	recommended horizontal size in pixels for subscripts 	 
	public int ySubscriptYSize;				// 	recommended vertical size in pixels for subscripts 	 
	public int ySubscriptXOffset;			// 	recommended horizontal offset for subscripts 	 
	public int ySubscriptYOffset;			// 	recommended vertical offset form the baseline for subscripts 	 
	public int ySuperscriptXSize;			// 	recommended horizontal size in pixels for superscripts 	 
	public int ySuperscriptYSize;			// 	recommended vertical size in pixels for superscripts 	 
	public int ySuperscriptXOffset;			// 	recommended horizontal offset for superscripts 	 
	public int ySuperscriptYOffset;			// 	recommended vertical offset from the baseline for superscripts 	 
	public int yStrikeoutSize;				// 	width of the strikeout stroke 	 
	public int yStrikeoutPosition;			// 	position of the strikeout stroke relative to the baseline 	 
	public int sFamilyClass0;				// 	classification of font-family design.
	public int sFamilyClass1;				// 	classification of font-family design. 
	public int panose1;						//	10 byte series of number used to describe the visual characteristics of a given typeface
	public int panose2;
	public int panose3;
	public int panose4;
	public int panose5;
	public int panose6;
	public int panose7;
	public int panose8;
	public int panose9;
	public int panose10;
	public long ulCharRange1;				// 	Field is split into two bit fields of 96 and 36 bits each. The low 96 bits are used to specify the Unicode blocks encompassed by the font file. The high 32 bits are used to specify the character or script sets covered by the font file. Bit assignments are pending. Set to 0 
	public long ulCharRange2;
	public long ulCharRange3;
	public long ulCharRange4;
	public byte[] achVendID = new byte[4]; 	//	four character identifier for the font vendor 	 
	public int fsSelection;					//	2-byte bit field containing information concerning the nature of the font patterns 	 
	public int fsFirstCharIndex;			// 	The minimum Unicode index in this font. 	 
	public int fsLastCharIndex;				// 	The maximum Unicode index in this font.
	//////////////////////////////////////////  MS Specific:
	public int sTypoAscender; 	 
	public int sTypoDescender; 	 
	public int sTypoLineGap; 	 
	public int usWinAscent; 	 
	public int usWinDescent; 	 
	public long ulCodePageRange1;		 	// Bits 0-31
	public long ulCodePageRange2; 			// Bits 32-63
	public int sxHeight; 	 
	public int sCapHeight; 	 
	public int usDefaultChar; 	 
	public int usBreakChar; 	 
	public int usMaxContext;
	
	private final static long UINT_MASK = (1L << 32)-1L;
	private Box box = null;
	private final static HashMap<Character, Integer> xAvgData = new HashMap<Character, Integer>();
		
	static {
		xAvgData.put('a',64);
		xAvgData.put('b',14);
		xAvgData.put('c',27);
		xAvgData.put('d',35);
		xAvgData.put('e',100);
		xAvgData.put('f',20);
		xAvgData.put('g',14);
		xAvgData.put('h',42);
		xAvgData.put('i',63);
		xAvgData.put('j',3);
		xAvgData.put('k',6);
		xAvgData.put('l',35);
		xAvgData.put('m',20);
		xAvgData.put('n',56);
		xAvgData.put('o',56);
		xAvgData.put('p',17);
		xAvgData.put('q',4);
		xAvgData.put('r',49);
		xAvgData.put('s',56);
		xAvgData.put('t',71);
		xAvgData.put('u',31);
		xAvgData.put('v',10);
		xAvgData.put('w',18);
		xAvgData.put('x',3);
		xAvgData.put('y',18);
		xAvgData.put('z',2);
		xAvgData.put(' ',166);
	}
		
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.version = ttf.readUnsignedShort();
		this.xAvgCharWidth = ttf.readShort();
		this.usWeightClass = ttf.readUnsignedShort();
		this.usWidthClass = ttf.readUnsignedShort();
		this.fsType = ttf.readUnsignedShort();
		this.ySubscriptXSize = ttf.readShort();
		this.ySubscriptYSize = ttf.readShort();
		this.ySubscriptXOffset = ttf.readShort();
		this.ySubscriptYOffset = ttf.readShort();
		this.ySuperscriptXSize = ttf.readShort();
		this.ySuperscriptYSize = ttf.readShort();
		this.ySuperscriptXOffset = ttf.readShort();
		this.ySuperscriptYOffset = ttf.readShort();
		this.yStrikeoutSize = ttf.readShort();
		this.yStrikeoutPosition = ttf.readShort();
		this.sFamilyClass0 = ttf.readUnsignedByte();
		this.sFamilyClass1 = ttf.readUnsignedByte();
		this.panose1 = ttf.readUnsignedByte();
		this.panose2 = ttf.readUnsignedByte();
		this.panose3 = ttf.readUnsignedByte();
		this.panose4 = ttf.readUnsignedByte();
		this.panose5 = ttf.readUnsignedByte();
		this.panose6 = ttf.readUnsignedByte();
		this.panose7 = ttf.readUnsignedByte();
		this.panose8 = ttf.readUnsignedByte();
		this.panose9 = ttf.readUnsignedByte();
		this.panose10= ttf.readUnsignedByte();
		this.ulCharRange1 = ttf.readInt() & UINT_MASK;
		this.ulCharRange2 = ttf.readInt() & UINT_MASK;
		this.ulCharRange3 = ttf.readInt() & UINT_MASK;
		this.ulCharRange4 = ttf.readInt() & UINT_MASK;
		ttf.readFully(this.achVendID);
		this.fsSelection = ttf.readUnsignedShort();
		this.fsFirstCharIndex = ttf.readUnsignedShort();
		this.fsLastCharIndex = ttf.readUnsignedShort();
		long off = ttf.getFilePointer() - my_offset;
		if (this.version >= VERSION_0_MS && off + 10 <= length) {
			this.sTypoAscender = ttf.readShort();
			this.sTypoDescender = ttf.readShort();
			this.sTypoLineGap = ttf.readShort();
			this.usWinAscent = ttf.readUnsignedShort();
			this.usWinDescent = ttf.readUnsignedShort();
		}
		if (this.version >= VERSION_1_MS) {
			this.ulCodePageRange1 = ttf.readInt() & UINT_MASK;
			this.ulCodePageRange2 = ttf.readInt() & UINT_MASK;
		}
		if (this.version >= VERSION_2_MS) {
			this.sxHeight = ttf.readShort();
			this.sCapHeight = ttf.readShort();
			this.usDefaultChar = ttf.readUnsignedShort();
			this.usBreakChar = ttf.readUnsignedShort();
			this.usMaxContext = ttf.readUnsignedShort();
		}		
		return true;
	}

	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeShort(this.version);
		ttf.writeShort(this.xAvgCharWidth);
		ttf.writeShort(this.usWeightClass);
		ttf.writeShort(this.usWidthClass);
		ttf.writeShort(this.fsType);
		ttf.writeShort(this.ySubscriptXSize);
		ttf.writeShort(this.ySubscriptYSize);
		ttf.writeShort(this.ySubscriptXOffset);
		ttf.writeShort(this.ySubscriptYOffset);
		ttf.writeShort(this.ySuperscriptXSize);
		ttf.writeShort(this.ySuperscriptYSize);
		ttf.writeShort(this.ySuperscriptXOffset);
		ttf.writeShort(this.ySuperscriptYOffset);
		ttf.writeShort(this.yStrikeoutSize);
		ttf.writeShort(this.yStrikeoutPosition);
		ttf.writeByte(this.sFamilyClass0);
		ttf.writeByte(this.sFamilyClass1);
		ttf.writeByte(this.panose1);
		ttf.writeByte(this.panose2);
		ttf.writeByte(this.panose3);
		ttf.writeByte(this.panose4);
		ttf.writeByte(this.panose5);
		ttf.writeByte(this.panose6);
		ttf.writeByte(this.panose7);
		ttf.writeByte(this.panose8);
		ttf.writeByte(this.panose9);
		ttf.writeByte(this.panose10);
		ttf.writeInt((int) this.ulCharRange1);
		ttf.writeInt((int) this.ulCharRange2);
		ttf.writeInt((int) this.ulCharRange3);
		ttf.writeInt((int) this.ulCharRange4);
		ttf.write(this.achVendID, 0, 4);
		ttf.writeShort(this.fsSelection);
		ttf.writeShort(this.fsFirstCharIndex);
		ttf.writeShort(this.fsLastCharIndex);
		ttf.writeShort(this.sTypoAscender);
		ttf.writeShort(this.sTypoDescender);
		ttf.writeShort(this.sTypoLineGap);
		ttf.writeShort(this.usWinAscent);
		ttf.writeShort(this.usWinDescent);
		if (this.version >= VERSION_1_MS) {
			ttf.writeInt((int) this.ulCodePageRange1);
			ttf.writeInt((int) this.ulCodePageRange2);
		}
		if (this.version >= VERSION_2_MS) {
			ttf.writeShort(this.sxHeight);
			ttf.writeShort(this.sCapHeight);
			ttf.writeShort(this.usDefaultChar);
			ttf.writeShort(this.usBreakChar);
			ttf.writeShort(this.usMaxContext);
		}
		this.finishWrite(ttf);
		return true;		
	}

	@Override
	public JComponent getView(Component c, Font f) {
		this.setupView();
		if (box==null) {
			box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(10));
			(new VControl("version","OS/2 table version",this,null)).pinInto(box);
			(new VControl("xAvgCharWidth","average width of character",this,VControl.SHORT)).pinInto(box);
			(new VControl("usWeightClass","visual weight","VW_",this,VControl.USHORT)).pinInto(box);
			(new VControl("usWidthClass","relative change from the normal aspect ratio","W_",this,VControl.USHORT)).pinInto(box);
			(new VControl("fsType","Legal rights for embedding","FS_",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySubscriptXSize","ySubscriptXSize",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySubscriptYSize","ySubscriptYSize",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySubscriptXOffset","ySubscriptXOffset",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySubscriptYOffset","ySubscriptYOffset",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySuperscriptXSize","ySuperscriptXSize",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySuperscriptYSize","ySuperscriptYSize",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySuperscriptXOffset","ySuperscriptXOffset",this,VControl.SHORT)).pinInto(box);
			(new VControl("ySuperscriptYOffset","ySuperscriptYOffset",this,VControl.SHORT)).pinInto(box);
			(new VControl("yStrikeoutSize","yStrikeoutSize",this,VControl.SHORT)).pinInto(box);
			(new VControl("yStrikeoutPosition","yStrikeoutPosition",this,VControl.SHORT)).pinInto(box);
			(new VControl("sFamilyClass0","IBM Font Class","FC_",this,VControl.UBYTE)).pinInto(box);
			(new VControl("sFamilyClass1","IBM Font Subclass","FSC_",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose1","panose [1]","PAN_1",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose2","panose [2]","PAN_2",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose3","panose [3]","PAN_3",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose4","panose [4]","PAN_4",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose5","panose [5]","PAN_5",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose6","panose [6]","PAN_6",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose7","panose [7]","PAN_7",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose8","panose [8]","PAN_8",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose9","panose [9]","PAN_9",this,VControl.UBYTE)).pinInto(box);
			(new VControl("panose10","panose [10]","PAN_10",this,VControl.UBYTE)).pinInto(box);
			(new VControl("ulCharRange1","ulCharRange1","UR1_",this,VControl.UINT)).pinInto(box);
			(new VControl("ulCharRange2","ulCharRange2","UR2_",this,VControl.UINT)).pinInto(box);
			(new VControl("ulCharRange3","ulCharRange3","UR3_",this,VControl.UINT)).pinInto(box);
			(new VControl("ulCharRange4","ulCharRange4","UR4_",this,VControl.UINT)).pinInto(box);
			(new VControl("achVendID","Vendor ID",this,VControl.BID4)).pinInto(box);
			(new VControl("fsSelection","fsSelection","FSEL_",this,VControl.USHORT)).pinInto(box);
			(new VControl("fsFirstCharIndex","fsFirstCharIndex",this,VControl.USHORT)).pinInto(box);
			(new VControl("fsLastCharIndex","fsLastCharIndex",this,VControl.USHORT)).pinInto(box);
			if (this.version == VERSION_3_MS) {
				(new VControl("sTypoAscender","sTypoAscender",this,VControl.SHORT)).pinInto(box);
				(new VControl("sTypoDescender","sTypoDescender",this,VControl.SHORT)).pinInto(box);
				(new VControl("sTypoLineGap","sTypoLineGap",this,VControl.SHORT)).pinInto(box);
				(new VControl("usWinAscent","usWinAscent",this,VControl.USHORT)).pinInto(box);
				(new VControl("usWinDescent","usWinDescent",this,VControl.USHORT)).pinInto(box);
				(new VControl("ulCodePageRange1","ulCodePageRange1",this,VControl.UINT)).pinInto(box);
				(new VControl("ulCodePageRange2","ulCodePageRange2",this,VControl.UINT)).pinInto(box);
				(new VControl("sxHeight","sxHeight",this,VControl.SHORT)).pinInto(box);
				(new VControl("sCapHeight","sCapHeight",this,VControl.SHORT)).pinInto(box);
				(new VControl("usDefaultChar","usDefaultChar",this,VControl.USHORT)).pinInto(box);
				(new VControl("usBreakChar","usBreakChar",this,VControl.USHORT)).pinInto(box);
				(new VControl("usMaxContext","usMaxContext",this,VControl.USHORT)).pinInto(box);
			}
			view.setLayout(new FlowLayout(FlowLayout.LEADING));
			view.add(Box.createHorizontalStrut(10));
			view.add(box);			
		}
		return spanel;
	}

	@Override
	public String getTableName() {
		return "OS/2";
	}
	
	public void notifyFieldChanges() {
		if (box!=null) {
			Component[] vcs = box.getComponents();
			for (Component c : vcs) {
				if (c instanceof VControl) {
					((VControl)c).updateField();
				}
			}
		}
	}
}
