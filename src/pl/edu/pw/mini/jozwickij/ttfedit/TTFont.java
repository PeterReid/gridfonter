package pl.edu.pw.mini.jozwickij.ttfedit;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFDirEntry;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFOffsetSubtable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTables;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.TTFTable_gasp;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_head;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.TTFTable_name;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;

public class TTFont {
	
	public final static String MODE_READ = "r";
	public final static String MODE_RWRITE = "rw";
	public final static String MODE_RWSYNC = "rws";
	public final static String TTF_EXT = ".ttf";
		
	/* tables need to be ordered by name */
	private Map<String,TTFTable> tables = Collections.synchronizedMap(new TreeMap<String,TTFTable>());
	private Map<String,TTFTable> schedTables = Collections.synchronizedMap(new TreeMap<String,TTFTable>());
	private RandomAccessFile ttf = null;
	private String filename = null;
	private TTFOffsetSubtable offsets = null;
	private TTFDirEntry dirEntry = null;
	private static final Vector<String> blacklist = new Vector<String>();
	private static final Vector<String> graylist = new Vector<String>();
	
	static {
		blacklist.add(TTFTables.OFFSET_SUBTABLE);
		blacklist.add(TTFTables.DSIG);
	}
	
	static {
		graylist.add(TTFTables.CVT);
		graylist.add(TTFTables.FPGM);
		graylist.add(TTFTables.PREP);
		graylist.add(TTFTables.LTSH);
		graylist.add(TTFTables.VDMX);
		graylist.add(TTFTables.HDMX);
		graylist.add("PCLT");
	}
	
	private void addTable(TTFTable tab) {
		tables.put(tab.getTableName(), tab);
	}
	
	private void scheduleTable(TTFTable tab) {
		schedTables.put(tab.getTableName(), tab);
	}
	
	private void readOffsets() throws Exception
	{
		offsets = new TTFOffsetSubtable();
		offsets.readFrom(ttf, 0, 12, 0, tables);
		addTable(offsets);		
	}
	
	public TTFont(String file, String mode) throws Exception
	{
		if (mode.length()<1)
			mode = MODE_READ;
		try {
			ttf = new RandomAccessFile(filename=file, mode);
			readOffsets();
			dirEntry = new TTFDirEntry();
			Debug.println("Processing file '"+filename+"' in mode '" + mode +"'",this);
		}
		catch (Exception e) { throw e; }		
		
		boolean done = false;
		
		for (int i=0; i<offsets.numTables; i++)	{
			try {
				done = dirEntry.processEntryIn(ttf,i,tables);				
			}
			catch (Exception e) {
				throw e;				
			}
			
			if (done) {
				addTable( dirEntry.getLastTable() );
			}
			else {
				Debug.println("Table "+dirEntry.getLastTag()+" needs to be processed later",this);
				scheduleTable( dirEntry.getLastTable() );
			}
		}
		
		for (int i=0; i<DefaultProperties.DEP_RETRY_COUNT; i++) {
			if (schedTables.size()==0)
				break;
			
			for (Entry<String,TTFTable> e : schedTables.entrySet()) {
			
				boolean is_done = false;
				try {
					is_done = e.getValue().reread(ttf,tables);					
				}
				catch (Exception ex) {
					throw ex;
				}
				if (is_done) {
					addTable( e.getValue() );
					schedTables.remove(e.getKey());
					i--;
					break;
				}
				else {
					Debug.printlnErr("Table "+e.getValue().getTableName()+" needs to be processed later (because of dependencies)",this);
				}
			}
		}
	}
	
	public TTFTable getTable(String name)  {
		return tables.get(name);
	}
	
	public Set<Entry<String, TTFTable>> getTables() {
		return tables.entrySet();
	}
	
	public Map<String, TTFTable> getTablesMap() {
		return tables;
	}
	
	synchronized public void destroy() {
		try {
			ttf.close();
		}
		catch (Exception e) {}
		
		tables.clear();
		schedTables.clear();
		offsets = null;
		dirEntry = null;
	}
	
	public String getDescription() {
		TTFTable name = tables.get(TTFTables.NAME);
		if (name==null || !(name instanceof TTFTable_name) )
			return "Font name not defined";
		return ((TTFTable_name)name).getDescription();
	}
	
	public static long alignTo4(RandomAccessFile ttf) throws Exception {
		long fp = ttf.getFilePointer();
		fp = ( (fp+3)/4 )*4;
		ttf.seek(fp);
		return fp;
	}
	
	public final static boolean isTableOK(String tag) {
		if (!DefaultProperties.PRESERVE_TTF_ASM && graylist.contains(tag) ) {
			return false;
		}
		return tag.length()==4 && !blacklist.contains(tag);				
	}

	synchronized public void save(String path) throws Exception {
		if (!path.toLowerCase().endsWith(TTF_EXT)) {
			path+= TTF_EXT;
		}
		(new File(path)).delete();
		ttf = new RandomAccessFile(path, TTFont.MODE_RWRITE);
		/* Gasp hack for hinting */
		TTFTable gasp = tables.get(TTFTables.GASP);
		if (gasp==null && !DefaultProperties.PRESERVE_TTF_ASM) {
			tables.put(TTFTables.GASP, new TTFTable_gasp());
		}
		offsets.writeToFile(ttf, tables);
		
		int cnt = 0;
				
		for (Entry<String,TTFTable> ent : tables.entrySet()) {
			ent.getValue().notifyWrite(tables);	
		}
		
		for (Entry<String,TTFTable> ent : tables.entrySet()) {
			if (isTableOK(ent.getKey())) {
				TTFTable tab = ent.getValue();
				tab.writeToFile(ttf,tables);
				dirEntry.processEntryOut(ttf,cnt++,tab);				
			}			
		}
		
		TTFTable_head head = ((TTFTable_head) tables.get(TTFTables.HEAD));
		if (head!=null)
			head.fixChecksum(ttf);
		ttf.close();		
	}
}
