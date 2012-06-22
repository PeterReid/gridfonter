package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.util.Map;
import java.util.TreeMap;

import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_DSIG;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_EBSC;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_LTSH;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_VDMX;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_Zapf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_acnt;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_avar;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_bdat;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_bhed;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_bloc;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_bsln;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_cvar;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_cvt;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_fdsc;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_feat;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_fmtx;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_fpgm;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_fvar;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_gasp;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_gvar;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_hdmx;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_hsty;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_just;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_kern;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_lcar;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_mort;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_morx;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_opbd;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_prep;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_prop;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_trak;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_vhea;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_vmtx;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_OS_2;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_cmap;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_glyf;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_head;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_hhea;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_hmtx;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_loca;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_maxp;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_name;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_post;

public final class TTFTables {
	public final static String ACNT = "acnt"; /* accent attachment */
	public final static String AVAR = "avar"; /* axis variation */
	public final static String BDAT = "bdat"; /* bitmap data */
	public final static String BHED = "bhed"; /* bitmap font header */
	public final static String BLOC = "bloc"; /* bitmap location */
	public final static String BSLN = "bsln"; /* baseline */
	public final static String CMAP = "cmap"; /* character code mapping */
	public final static String CVAR = "cvar"; /* CVT variation */
	public final static String CVT  = "cvt "; /* control value table */
	public final static String DSIG = "DSIG"; /* digital signature */
	public final static String EBSC = "EBSC"; /* embedded bitmap scaling control */
	public final static String FDSC = "fdsc"; /* font descriptor */
	public final static String FEAT = "feat"; /* layout feature */
	public final static String FMTX = "fmtx"; /* font metrics */
	public final static String FPGM = "fpgm"; /* font program */
	public final static String FVAR = "fvar"; /* font variation */
	public final static String GASP = "gasp"; /* grid-fitting and scan-conversion procedure */
	public final static String GLYF = "glyf"; /* glyph outline */
	public final static String GVAR = "gvar"; /* glyph variation */
	public final static String HDMX = "hdmx"; /* horizontal device metrics */
	public final static String HEAD = "head"; /* font header */
	public final static String HHEA = "hhea"; /* horizontal header */
	public final static String HMTX = "hmtx"; /* horizontal metrics */
	public final static String HSTY = "hsty"; /* horizontal style */
	public final static String JUST = "just"; /* justification */
	public final static String KERN = "kern"; /* kerning */
	public final static String LCAR = "lcar"; /* ligature caret */
	public final static String LOCA = "loca"; /* glyph location */
	public final static String MAXP = "maxp"; /* maximum profile */
	public final static String MORT = "mort"; /* metamorphosis */
	public final static String MORX = "morx"; /* extended metamorphosis */
	public final static String NAME = "name"; /* name */
	public final static String OPDB	= "opbd"; /* optical bounds */
	public final static String OS_2 = "OS/2"; /* compatibility */
	public final static String POST = "post"; /* glyph name and PostScript compatibility */
	public final static String PREP = "prep"; /* control value program */
	public final static String PROP = "prop"; /* properties */
	public final static String TRAK = "trak"; /* tracking */
	public final static String VHEA = "vhea"; /* vertical header */
	public final static String VMTX = "vmtx"; /* vertical metrics */
	public final static String ZAPF = "Zapf"; /* glyph reference */
	public final static String LTSH = "LTSH"; /* MS Linear Threshold */
	public final static String VDMX = "VDMX"; /* MS Vertical Device Metrics */
	
	public final static String OFFSET_SUBTABLE = "TTFOffsetSubtable";
	public final static String DIRECTORY_ENTRY = "TTFDirEntry";
	
	private final static String[] TABS_REQUIRED = { CMAP, GLYF, HEAD, HHEA, HMTX,
		   LOCA, MAXP, NAME, POST, OS_2 };
	/* Windows needs OS_2, but OSX doesn't */

	public final static int PADDING = 3;
	private static TreeMap<String,Class> TTF_TABLES = new TreeMap<String,Class>();
	
	static {
		TTF_TABLES.put(ACNT, TTFTable_acnt.class);
		TTF_TABLES.put(AVAR, TTFTable_avar.class);
		TTF_TABLES.put(BDAT, TTFTable_bdat.class);
		TTF_TABLES.put(BHED, TTFTable_bhed.class);
		TTF_TABLES.put(BLOC, TTFTable_bloc.class);
		TTF_TABLES.put(BSLN, TTFTable_bsln.class);
		TTF_TABLES.put(CMAP, TTFTable_cmap.class);
		TTF_TABLES.put(CVAR, TTFTable_cvar.class);
		TTF_TABLES.put(CVT,  TTFTable_cvt.class);
		TTF_TABLES.put(DSIG, TTFTable_DSIG.class);
		TTF_TABLES.put(EBSC, TTFTable_EBSC.class);
		TTF_TABLES.put(FDSC, TTFTable_fdsc.class);
		TTF_TABLES.put(FEAT, TTFTable_feat.class);
		TTF_TABLES.put(FMTX, TTFTable_fmtx.class);
		TTF_TABLES.put(FPGM, TTFTable_fpgm.class);
		TTF_TABLES.put(FVAR, TTFTable_fvar.class);
		TTF_TABLES.put(GASP, TTFTable_gasp.class);
		TTF_TABLES.put(GLYF, TTFTable_glyf.class);
		TTF_TABLES.put(GVAR, TTFTable_gvar.class);
		TTF_TABLES.put(HDMX, TTFTable_hdmx.class);
		TTF_TABLES.put(HEAD, TTFTable_head.class);
		TTF_TABLES.put(HHEA, TTFTable_hhea.class);
		TTF_TABLES.put(HMTX, TTFTable_hmtx.class);
		TTF_TABLES.put(HSTY, TTFTable_hsty.class);
		TTF_TABLES.put(JUST, TTFTable_just.class);
		TTF_TABLES.put(KERN, TTFTable_kern.class);
		TTF_TABLES.put(LCAR, TTFTable_lcar.class);
		TTF_TABLES.put(LOCA, TTFTable_loca.class);
		TTF_TABLES.put(MAXP, TTFTable_maxp.class);
		TTF_TABLES.put(MORT, TTFTable_mort.class);
		TTF_TABLES.put(MORX, TTFTable_morx.class);
		TTF_TABLES.put(NAME, TTFTable_name.class);
		TTF_TABLES.put(OPDB, TTFTable_opbd.class);
		TTF_TABLES.put(OS_2, TTFTable_OS_2.class);
		TTF_TABLES.put(POST, TTFTable_post.class);
		TTF_TABLES.put(PREP, TTFTable_prep.class);
		TTF_TABLES.put(PROP, TTFTable_prop.class);
		TTF_TABLES.put(TRAK, TTFTable_trak.class);
		TTF_TABLES.put(VHEA, TTFTable_vhea.class);
		TTF_TABLES.put(VMTX, TTFTable_vmtx.class);
		TTF_TABLES.put(ZAPF, TTFTable_Zapf.class);
		TTF_TABLES.put(LTSH, TTFTable_LTSH.class);
		TTF_TABLES.put(VDMX, TTFTable_VDMX.class);
		
		TTF_TABLES.put(OFFSET_SUBTABLE, TTFOffsetSubtable.class);
		TTF_TABLES.put(DIRECTORY_ENTRY, TTFDirEntry.class);
	}
	
	/**
	 * Return class for given table name. Used when creating new class instance
	 * @param name Name of table - constant from TTFTables
	 * @return Class matching given table name (may be null)
	 */
	public static Class getTableClass(String name) {
		return TTF_TABLES.get(name);
	}
	
	public static void checkRequiredTables(Map<String,TTFTable> tables) throws Exception {
		for (String name : TABS_REQUIRED) {
			if (tables.get(name)==null) {
				throw new Exception("Missing table "+name+"!");
			}
		}
	}
}
