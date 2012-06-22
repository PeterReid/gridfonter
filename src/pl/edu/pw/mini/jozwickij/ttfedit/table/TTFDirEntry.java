package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;


public class TTFDirEntry extends TTFTable implements Comparable<TTFDirEntry> {
	
	public final static long UINT_MASK = 0x00000000FFFFFFFF;
	
	private String tag;			/* 4-byte identifier */
	private int checkSum = -1;	/* checksum for this table */
	private int offset = -1;	/* offset from beginning of sfnt */
	private int length = -1;	/* length of this table in byte (actual length not padded length) */
	private byte b[] = new byte[4];
	private TTFTable tab = null;
		
	public final static int calcTableChecksum(byte[] bytes, int length)
    {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		
		long sum = 0;
		int len = 0;
		try {
			for (int i=0; i < (length+3)/4; i++) {
				sum = (sum + dis.readInt()) & UINT_MASK;
				len += 4;				
			}
			dis.close();
			bais.close();
		}
		catch (IOException e) { Debug.printlnErr("len="+len+",length="+bytes.length+" "+e); }		
		return (int)(sum & UINT_MASK);
    }

	public boolean processEntryIn(RandomAccessFile ttf, int i, Map<String, TTFTable> tables) throws Exception {
		ttf.seek(12+i*16);
		ttf.readFully(b);
        tag = new String(b);
        checkSum = ttf.readInt();
        offset = ttf.readInt();
        length = ttf.readInt();
        Debug.println("Tag="+tag+",offset="+offset+",length="+length+",chksum="+checkSum,this);
        Class c = TTFTables.getTableClass(tag);
        if (c==null) {
        	Debug.printlnErr("No table handler for "+tag+"!",this);
        	tab = new TTFTableUnknown(tag);
        }
        else {
        	tab = (TTFTable) c.newInstance();
        }
        int sum = tab.getChecksum(ttf,offset,length);
		if (sum!=checkSum)
			Debug.printlnErr("CHECKSUM is="+sum+", SHOULD be="+checkSum+" ==>"+tab.getTableName(),this);
		Debug.println("Processing table "+tag,this);
		tab.init(ttf,offset,length,sum);
		return tab.readFrom(ttf, offset, length, checkSum, tables);
	}
	
	public boolean processEntryOut(RandomAccessFile ttf, int i, TTFTable tab) throws Exception {
		long fp = ttf.getFilePointer();
		ttf.seek(12+i*16);
		ttf.write(tab.getTableName().substring(0,4).getBytes("ASCII"));
		ttf.writeInt(tab.my_checksum);
		ttf.writeInt(tab.my_offset);
		ttf.writeInt(tab.my_length);
		ttf.seek(fp);
		return true;
	}
	
	public String getLastTag() {
		return tag;
	}
	
	public TTFTable getLastTable() {
		return tab;
	}

	public int compareTo(TTFDirEntry o) {
		return this.tag.compareTo(o.tag);
	}
}
