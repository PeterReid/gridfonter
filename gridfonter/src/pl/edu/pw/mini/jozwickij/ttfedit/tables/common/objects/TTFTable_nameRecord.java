package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JTextField;

import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_name;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFTable_nameRecord implements Comparable<TTFTable_nameRecord>
{
	public final static int PLATID_UNICODE = 0; //	Indicates Unicode version.
	public final static int PLATID_MACINTOSH = 1; //Script Manager code.
	public final static int PLATID_RESERVED = 2; // do not use
	public final static int PLATID_MICROSOFT = 3; //Microsoft encoding.
	public final static int PLATSID_MICROSOFT = 1;
	public final static int PLATLANGID_MICROSOFT = 0x409;
	public final static String VERSION_ = "version ";
	public final static String VERSION_2_40 = "Version 2.40";
	private static String tmpName = null;
	private static String tmpFamily = null;
		
	private final static String PLATSID_MACINTOSH_Names[] = {
		"Roman", //0
		"Japanese",
		"Traditional Chinese",
		"Korean",
		"Arabic",
		"Hebrew",
		"Greek",
		"Russian",
		"RSymbol",
		"Devanagari",
		"Gurmukhi",
		"Gujarati",
		"Oriya",
		"Bengali",
		"Tamil",
		"Telugu",
		"Kannada",
		"Malayalam",
		"Sinhalese",
		"Burmese",
		"Khmer",
		"Thai",
		"Laotian",
		"Georgian",
		"Armenian",
		"Simplified Chinese",
		"Tibetan",
		"Mongolian",
		"Geez",
		"Slavic",
		"Vietnamese",
		"Sindhi",
		"(Uninterpreted)"
	};
	
	private final static String PLATSID_UNICODE_Names[] = {
		"Default semantics", //0
		"Version 1.1 semantics",
		"ISO 10646 1993 semantics (deprecated)",
		"2.0 or later semantics"
	};
	
	private final static String PLATSID_MICROSOFT_Names[] = {
		"Undefined character set or indexing scheme", //0
		"UGL character set with Unicode indexing scheme",		
	};
	
	private final static String LANGID_MACINTOSH_Names[] = {
		"English", //0
		"French",
		"German",
		"Italian",
		"Dutch",
		"Swedish",
		"Spanish",
		"Danish",
		"Portuguese",
		"Norwegian",
		"Hebrew",
		"Japanese",
		"Arabic",
		"Finnish",
		"Greek",
		"Icelandic",
		"Maltese",
		"Turkish",
		"Croatian",
		"Chinese (traditional)",
		"Urdu",
		"Hindi",
		"Thai",
		"Korean",
		"Lithuanian",
		"Polish",
		"Hungarian",
		"Estonian",
		"Latvian",
		"Sami",
		"Faroese",
		"Farsi/Persian",
		"Russian",
		"Chinese (simplified)",
		"Flemish",
		"Irish Gaelic",
		"Albanian",
		"Romanian",
		"Czech",
		"Slovak",
		"Slovenian",
		"Yiddish",
		"Serbian",
		"Macedonian",
		"Bulgarian",
		"Ukrainian",
		"Byelorussian",
		"Uzbek",
		"Kazakh",
		"Azerbaijani (Cyrillic script)",
		"Azerbaijani (Arabic script)",
		"Armenian",
		"Georgian",
		"Moldavian",
		"Kirghiz",
		"Tajiki",
		"Turkmen",
		"Mongolian (Mongolian script)",
		"Mongolian (Cyrillic script)",
		"Pashto",
		"Kurdish",
		"Kashmiri",
		"Sindhi",
		"Tibetan",
		"Nepali",
		"Sanskrit",
		"Marathi",
		"Bengali",
		"Assamese",
		"Gujarati",
		"Punjabi",
		"Oriya",
		"Malayalam",
		"Kannada",
		"Tamil",
		"Telugu",
		"Sinhalese",
		"Burmese",
		"Khmer",
		"Lao",
		"Vietnamese",
		"Indonesian",
		"Tagalog",
		"Malay (Roman script)",
		"Malay (Arabic script)",
		"Amharic",
		"Tigrinya",
		"Galla",
		"Somali",
		"Swahili",
		"Kinyarwanda/Ruanda",
		"Rundi",
		"Nyanja/Chewa",
		"Malagasy",
		"Esperanto",
		"Welsh",
		"Basque",
		"Catalan",
		"Latin",
		"Quechua",
		"Guarani",
		"Aymara",
		"Tatar",
		"Uighur",
		"Dzongkha",
		"Javanese (Roman script)",
		"Sundanese (Roman script)",
		"Galician",
		"Afrikaans",
		"Breton",
		"Inuktitut",
		"Scottish Gaelic",
		"Manx Gaelic",
		"Irish Gaelic (with dot above)",
		"Tongan",
		"Greek (polytonic)",
		"Greenlandic",
		"Azerbaijani (Roman script)"
	};
	
	private final static TreeMap<Integer,String> LANGID_MICROSOFT_Names = new TreeMap<Integer,String>();
	static {
		LANGID_MICROSOFT_Names.put(0x041c,"Albanian");
		LANGID_MICROSOFT_Names.put(0x042d,"Basque");
		LANGID_MICROSOFT_Names.put(0x0423,"Byelorussian");
		LANGID_MICROSOFT_Names.put(0x0402,"Bulgarian");
		LANGID_MICROSOFT_Names.put(0x0403,"Catalan");
		LANGID_MICROSOFT_Names.put(0x041a,"Croatian");
		LANGID_MICROSOFT_Names.put(0x0405,"Czech");
		LANGID_MICROSOFT_Names.put(0x0406,"Danish");
		LANGID_MICROSOFT_Names.put(0x0413,"Dutch (Standard)");
		LANGID_MICROSOFT_Names.put(0x0813,"Belgian (Flemish)");
		LANGID_MICROSOFT_Names.put(0x0409,"American English");
		LANGID_MICROSOFT_Names.put(0x0809,"British English");
		LANGID_MICROSOFT_Names.put(0x0c09,"Australian English");
		LANGID_MICROSOFT_Names.put(0x1009,"Canadian English");
		LANGID_MICROSOFT_Names.put(0x1409,"New Zealand English");
		LANGID_MICROSOFT_Names.put(0x1809,"Ireland English");
		LANGID_MICROSOFT_Names.put(0x0425,"Estonian");
		LANGID_MICROSOFT_Names.put(0x040b,"Finnish");
		LANGID_MICROSOFT_Names.put(0x040c,"French");
		LANGID_MICROSOFT_Names.put(0x080c,"French Belgian");
		LANGID_MICROSOFT_Names.put(0x0c0c,"French Canadian");
		LANGID_MICROSOFT_Names.put(0x100c,"French Swiss");
		LANGID_MICROSOFT_Names.put(0x0407,"German");
		LANGID_MICROSOFT_Names.put(0x0807,"German Swiss");
		LANGID_MICROSOFT_Names.put(0x0c07,"German Austrian");
		LANGID_MICROSOFT_Names.put(0x1007,"German Luxembourg");
		LANGID_MICROSOFT_Names.put(0x140c,"German Liechtenstein");
		LANGID_MICROSOFT_Names.put(0x0408,"Greek");
		LANGID_MICROSOFT_Names.put(0x040e,"Hungarian");
		LANGID_MICROSOFT_Names.put(0x040f,"Icelandic");
		LANGID_MICROSOFT_Names.put(0x0410,"Italian");
		LANGID_MICROSOFT_Names.put(0x0810,"Italian Swiss");
		LANGID_MICROSOFT_Names.put(0x0426,"Latvian");
		LANGID_MICROSOFT_Names.put(0x0427,"Lithuanian");
		LANGID_MICROSOFT_Names.put(0x0414,"Norwegian (Bokmal)");
		LANGID_MICROSOFT_Names.put(0x0814,"Norwegian (Nynorsk)");
		LANGID_MICROSOFT_Names.put(0x0415,"Polish");
		LANGID_MICROSOFT_Names.put(0x0416,"Portuguese (Brazilian)");
		LANGID_MICROSOFT_Names.put(0x0816,"Portuguese");
		LANGID_MICROSOFT_Names.put(0x0418,"Romania");
		LANGID_MICROSOFT_Names.put(0x0419,"Russian");
		LANGID_MICROSOFT_Names.put(0x041b,"Slovak");
		LANGID_MICROSOFT_Names.put(0x0424,"Slovenian");
		LANGID_MICROSOFT_Names.put(0x040a,"Spanish (Traditional Sort)");
		LANGID_MICROSOFT_Names.put(0x080c,"Mexican");
		LANGID_MICROSOFT_Names.put(0x0c0c,"Spanish (Modern Sort)");		
	}
	
	private final static String NAMEID_Names[] = { // Apple's info
		"Copyright notice", //0
		"Font Family. This string is the font family name the user sees",
		"Font Subfamily. This string is the font family the user sees",
		"Unique subfamily identification",
		"Full name of the font",
		"Version of the name table",
		"PostScript name of the font. Note: A font may have only one PostScript name and that name must be ASCII",
		"Trademark notice",
		"Manufacturer name",
		"Designer; name of the designer of the typeface",
		"Description; description of the typeface. Can contain revision information, usage recommendations, history, features, and so on.",
		"URL of the font vendor (with procotol, e.g., http://, ftp://). If a unique serial number is embedded in the URL, it can be used to register the font",
		"URL of the font designer (with protocol, e.g., http://, ftp://)",
		"License description; description of how the font may be legally used, or different example scenarios for licensed use. This field should be written in plain language, not legalese",
		"License information URL, where additional licensing information can be found",
		"Reserved",
		"Preferred Family (Windows only); In Windows, the Family name is displayed in the font menu; the Subfamily name is presented as the Style name. For historical reasons, font families have contained a maximum of four styles, but font designers may group more than four fonts to a single family. The Preferred Family and Preferred Subfamily IDs allow font designers to include the preferred family/subfamily groupings. These IDs are only present if they are different from IDs 1 and 2",
		"Preferred Subfamily (Windows only); In Windows, the Family name is displayed in the font menu; the Subfamily name is presented as the Style name. For historical reasons, font families have contained a maximum of four styles, but font designers may group more than four fonts to a single family. The Preferred Family and Preferred Subfamily IDs allow font designers to include the preferred family/subfamily groupings. These IDs are only present if they are different from IDs 1 and 2",
		"Compatible Full (Macintosh only); On the Macintosh, the menu name is constructed using the FOND resource. This usually matches the Full Name. If you want the name of the font to appear differently than the Full Name, you can insert the Compatible Full Name in ID 18. This name is not used by the Mac OS itself, but may be used by application developers (e.g., Adobe).",
		"Sample text. This can be the font name, or any other text that the designer thinks is the best sample text to show what the font looks like"
	};
	
	public int platformID = 0; 			// Platform identifier code.
	public int platformSpecificID = 3; 	// Platform-specific encoding identifier.
	public int languageID = 0; 			// Language identifier.
	public int nameID = -1; 			// Name identifiers.
	public int length = 0; 				// Name string length in bytes.
	public int offset = 0; 				// Name string offset in bytes from stringOffset.
	public String name = "";
	private JTextField textField = null;
	
	public TTFTable_nameRecord(RandomAccessFile ttf, int offset) throws Exception {
		this.platformID = ttf.readUnsignedShort();
		this.platformSpecificID = ttf.readUnsignedShort();
		this.languageID = ttf.readUnsignedShort();
		this.nameID = ttf.readUnsignedShort();
		this.length = ttf.readUnsignedShort();
		this.offset = ttf.readUnsignedShort();
		long fp = ttf.getFilePointer();
		byte[] bName = new byte[length];
		ttf.seek(this.offset+offset);
		if (bName.length==0)
			name = "";
		else {
			try {
				ttf.readFully(bName);
				name = ((bName[0] & 0xff)>5) ? new String(bName) : new String(bName,"UTF-16");
			}
			catch (Exception e) {
				name = "";
				Debug.printlnErr("Error reading name record of size "+length+": "+e,this);
			}
		}
		ttf.seek(fp);		
	}

	public TTFTable_nameRecord(int id, String val) {
		this.nameID = id;
		this.name = val;
	}

	public int write(RandomAccessFile ttf, int storageStart, int offsetName) throws IOException {
		if (textField!=null)
			this.name = textField.getText();
		if (nameID == TTFTable_name.FAMILY_ID && tmpFamily!=null)
			name = tmpFamily;
		else if (nameID == TTFTable_name.NAME_ID && tmpName!=null)
			name = tmpName;
		else if (nameID == TTFTable_name.PSNAME_ID && tmpName!=null)
			name = tmpName;
						
		boolean unicode = platformID==0 || platformID==3;
		byte[] b = name.getBytes( unicode ? "UTF-16" : "ISO-8859-1");
		ttf.writeShort(this.platformID);
		ttf.writeShort(this.platformSpecificID);
		ttf.writeShort(this.languageID);
		ttf.writeShort(this.nameID);
				
		/* Unicode BOM 0xFEFF needs to be omitted */
		if (name.length()>0) {
			ttf.writeShort(this.length= unicode ? b.length-2 : b.length);
			ttf.writeShort(this.offset= unicode ? offsetName+2 : offsetName);
		}
		else {
			ttf.writeInt(0);
		}
		long fp = ttf.getFilePointer();
		ttf.seek(storageStart+offsetName);
		ttf.write(b);
		int nextOffset = (int)(ttf.getFilePointer() - storageStart);
		ttf.seek(fp);
		return nextOffset;
	}

	public void assignTextField(JTextField jtf) {
		this.textField = jtf;		
	}
	
	private static String getFromTab(int index, String[] tab, String defVal) {
		if (index < tab.length)
			return tab[index];
		else
			return defVal;
	}
	
	private String getFromTab(int index, Map<Integer,String> map, String defVal) {
		String val = map.get(index);
		return (val!=null) ? val : defVal;
	}
	
	public String getRecordDesc() {
		StringBuilder sb = new StringBuilder();
		switch (this.platformID) {
			case PLATID_MACINTOSH:
				sb.append("Macintosh, ");
				sb.append(getFromTab(platformSpecificID, PLATSID_MACINTOSH_Names, "Unknown Script")+", ");
				sb.append(getFromTab(languageID, LANGID_MACINTOSH_Names, "Unknown language")+" ");
				break;
			case PLATID_MICROSOFT:
				sb.append("Microsoft, ");
				sb.append(getFromTab(platformSpecificID, PLATSID_MICROSOFT_Names, "Unknown Script")+", ");
				sb.append(getFromTab(languageID, LANGID_MICROSOFT_Names, "Unknown language")+" ");
				break;
			case PLATID_UNICODE:
				sb.append("Unicode, ");
				sb.append(getFromTab(platformSpecificID, PLATSID_UNICODE_Names, "Unknown specification")+" ");
				break;
			default:
				sb.append("Platform ID="+this.platformID+", "); break;
		}
		return (sb+"").trim();
	}
	
	public static String getRecordDesc(int pid, int psid) {
		StringBuilder sb = new StringBuilder();
		switch (pid) {
			case PLATID_MACINTOSH:
				sb.append("Macintosh, ");
				sb.append(getFromTab(psid, PLATSID_MACINTOSH_Names, "Unknown Script")+" ");
				break;
			case PLATID_MICROSOFT:
				sb.append("Microsoft, ");
				sb.append(getFromTab(psid, PLATSID_MICROSOFT_Names, "Unknown Script")+" ");
				break;
			case PLATID_UNICODE:
				sb.append("Unicode, ");
				sb.append(getFromTab(psid, PLATSID_UNICODE_Names, "Unknown specification")+" ");
				break;
			default:
				sb.append("Platform ID="+psid+", "); break;
		}
		return (sb+"").trim();
	}
	
	public String getNameDesc() {
		return getFromTab(nameID, TTFTable_nameRecord.NAMEID_Names, "Unknown property");		
	}
	
	public String getDescription() {
		return this.getNameDesc() + " (" + this.getRecordDesc() + ")";
	}

	public static void resetTempNames() {
		tmpName = null;
		tmpFamily = null;		
	}

	public static void makeTempNames() {
		tmpFamily = "Preview";
		tmpName = tmpFamily + " " + Calendar.getInstance().getTimeInMillis();		
	}

	public @Override Object clone() {
		TTFTable_nameRecord nrec = new TTFTable_nameRecord(nameID, name);
		return nrec;
	}

	public int compareTo(TTFTable_nameRecord o) {
		int d = (this.platformID - o.platformID)*1000000;
		d+= (this.platformSpecificID - o.platformSpecificID)*100000;
		d+= (this.languageID - o.languageID)*1000;
		d+= (this.nameID - o.nameID);
		return d;
	}
}
