package pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects;

import java.util.Vector;

public class TTFTable_cmapPL {
	
	public final static int SHIFT_0 = TTFTable_cmapMap.SHIFT_NONE;
	public final static int SHIFT_1 = TTFTable_cmapMap.SHIFT_FIX_YMIN;
	public final static int SHIFT_2 = TTFTable_cmapMap.SHIFT_SECOND_BY_HALF_FIRST_DOWN;
	public final static int SHIFT_3 = TTFTable_cmapMap.SHIFT_SECOND_BY_HALF_FIRST_UP;
	
	private static Vector<TTFTable_cmapMap> cmapPL = new Vector<TTFTable_cmapMap>();
	static {
		cmapPL.add( new TTFTable_cmapMap('\u0104', SHIFT_1, '\u0041','\u00b8',"Aogonek") ); // Ą = A + ,
		cmapPL.add( new TTFTable_cmapMap('\u0105', SHIFT_1, '\u0061','\u00b8',"aogonek") ); // ą = a + ,
		cmapPL.add( new TTFTable_cmapMap('\u0106', SHIFT_0, '\u0043','\u00b4',"Cacute")  ); // Ć = C + '
		cmapPL.add( new TTFTable_cmapMap('\u0107', SHIFT_0, '\u0063','\u00b4',"cacute")  ); // ć = c + '
		cmapPL.add( new TTFTable_cmapMap('\u0118', SHIFT_1, '\u0045','\u00b8',"Eogonek") ); // Ę = E + ,
		cmapPL.add( new TTFTable_cmapMap('\u0119', SHIFT_1, '\u0065','\u00b8',"eogonek") ); // ę = e + ,
		cmapPL.add( new TTFTable_cmapMap('\u0141', SHIFT_0, '\u004c','\u002d',"Lstroke") ); // Ł = L + -
		cmapPL.add( new TTFTable_cmapMap('\u0142', SHIFT_0, '\u006c','\u002d',"lstroke") ); // ł = l + -
		cmapPL.add( new TTFTable_cmapMap('\u0143', SHIFT_0, '\u004e','\u00b4',"Nacute")  ); // Ń = N + '
		cmapPL.add( new TTFTable_cmapMap('\u0144', SHIFT_0, '\u006e','\u00b4',"nacute")  ); // ń = n + '
		cmapPL.add( new TTFTable_cmapMap('\u00d3', SHIFT_0, '\u004f','\u00b4',"Oacute")  ); // Ó = O + '
		cmapPL.add( new TTFTable_cmapMap('\u00f3', SHIFT_0, '\u006f','\u00b4',"oacute")  ); // ó = o + '
		cmapPL.add( new TTFTable_cmapMap('\u015a', SHIFT_0, '\u0053','\u00b4',"Sacute")  ); // Ś = S + '
		cmapPL.add( new TTFTable_cmapMap('\u015b', SHIFT_0, '\u0073','\u00b4',"sacute")  ); // ś = s + '
		cmapPL.add( new TTFTable_cmapMap('\u0179', SHIFT_0, '\u005a','\u00b4',"Zacute")  ); // Ź = Z + '
		cmapPL.add( new TTFTable_cmapMap('\u017a', SHIFT_0, '\u007a','\u00b4',"zacute")  ); // ź = z + '
		cmapPL.add( new TTFTable_cmapMap('\u017b', SHIFT_0, '\u005a','\u02d9',"Zdot")    ); // Ż = Z + *
		cmapPL.add( new TTFTable_cmapMap('\u017c', SHIFT_0, '\u007a','\u02d9',"zdot")    ); // ż = z + *
	}
	
	public static Vector<TTFTable_cmapMap> getCmap() {
		return cmapPL;
	}
}
