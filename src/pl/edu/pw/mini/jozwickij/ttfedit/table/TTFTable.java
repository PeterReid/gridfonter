package pl.edu.pw.mini.jozwickij.ttfedit.table;

import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

/**
 * Specyfikacja producenta:
 * @link http://developer.apple.com/fonts/TTRefMan/RM06/Chap6.html
 * @author JJ
 * @version 0.1
 */
public class TTFTable extends TTObject {
		
	public final String TTFTable_ = "TTFTable_";	
	
	protected int my_offset = 0;
	protected int my_length = 0;
	protected int my_checksum = 0;
	protected JScrollPane spanel = null;
	protected JPanel view = null;
	protected byte[] buffer = null;
	
	private static Vector<String> optOrder = new Vector<String>();
	
	static {
		optOrder.add(TTFTables.HEAD);
		optOrder.add(TTFTables.HHEA);
		optOrder.add(TTFTables.MAXP);
		optOrder.add(TTFTables.OS_2);
		optOrder.add(TTFTables.LOCA);
		optOrder.add(TTFTables.HMTX);
	}
			
	public String getTableName()
	{
		String name= this.getClass().getName();
		name = (String) name.subSequence(name.lastIndexOf(".")+1,name.length());
		if (name.startsWith(TTFTable_)) {
			return name.replace(TTFTable_,"");
		}
		return name;
	}
	
	public boolean isViewUserFriendly() {
		return false;
	}
	
	public int getChecksum(RandomAccessFile ttf) throws Exception {
		Debug.printlnErr("+ my_cheksum="+this.my_checksum);
		Debug.printlnErr("CK="+my_checksum+" CK2="+this.getChecksum(ttf, my_offset, my_length));
		Debug.printlnErr("- my_cheksum="+this.my_checksum);
		return 0;
	}
	
	protected int getChecksum(RandomAccessFile ttf, int offset, int length) throws IOException
	{
		ttf.seek(offset);
		buffer = new byte[ length + TTFTables.PADDING ]; /* padding to 4 */
		Arrays.fill(buffer,(byte)0);
		ttf.readFully(buffer, 0, length);
		int sum = TTFDirEntry.calcTableChecksum(buffer, length);
		if (this.getTableName().compareTo(TTFTables.HEAD)==0) {
			ttf.seek(offset);
			if (ttf.skipBytes(8)!=8) {
				Debug.printlnErr("Checksum skip error",this);
			}
			sum -= ttf.readInt(); /* without checkSumAdjustment field */			
		}
		ttf.seek(offset+length);
		return sum;
	}
	
	public void init(RandomAccessFile ttf, int offset, int length, int checksum) throws Exception {
		this.my_offset = offset;
		this.my_length = length;
		this.my_checksum = checksum;
		ttf.seek(my_offset);
	}
	
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception
	{
		if (!Util.checkOverrideMethod(TTFTable.class,this.getClass(),"readFrom"))
			Debug.println("ReadFrom method not overriden in its table "+getTableName(),this);
		return true;
	}
	
	public boolean reread(RandomAccessFile ttf, Map<String,TTFTable> tables) throws Exception {
		ttf.seek(my_offset);
		Debug.println("Processing table "+this.getTableName(),this);
		return readFrom(ttf, my_offset, my_length, my_checksum, tables);
	}
	
	@Override
	public JComponent getView(Component c, Font font) {
		if (DefaultProperties.SHOW_TABLES) {
			if (view==null) {
				view = new JPanel();
				view.add(new JLabel(DefaultProperties.NO_TABLE_TEMPLATE));			
			}
			return view;
		}
		else {
			return null;
		}
	}
	
	protected void setupView() {
		if (view==null) {
			view = new JPanel();
			spanel = new JScrollPane(view);
			spanel.setBorder(new EmptyBorder(0,0,0,0));
			spanel.getVerticalScrollBar().setUnitIncrement(DefaultProperties.SCROLL_AMOUNT);
			spanel.getHorizontalScrollBar().setUnitIncrement(DefaultProperties.SCROLL_AMOUNT);
		}		
	}
	
	public String toString() {
		return getTableName();
	}
	
	public static boolean bit(int flag, int mask) {
		return (flag & mask)!=0;
	}
	
	protected float mid(int val1, int val2) {
		return (val1+val2)/2.0f;
	}
	
	protected long align4(RandomAccessFile ttf) throws Exception {
		return TTFont.alignTo4(ttf);
	}

	protected TTFTable prepareWrite(RandomAccessFile ttf) throws Exception {
		this.my_offset = (int) this.walign4(ttf);
		return this;
	}
	
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception
	{
		if (!Util.checkOverrideMethod(TTFTable.class,this.getClass(),"writeToFile"))
			Debug.printlnErr("WriteToFile method not overriden in its table "+getTableName(),this);
		this.prepareWrite(ttf);		
		ttf.write(this.buffer, 0, my_length);
		this.finishWrite(ttf);		
		return true;
	}
	
	protected TTFTable finishWrite(RandomAccessFile ttf) throws Exception {
		this.my_length = (int)(ttf.getFilePointer() - this.my_offset);
		this.my_checksum = this.getChecksum(ttf, my_offset, my_length);
		return this;
	}
	
	public void notifyWrite(Map<String, TTFTable> tables) {}

	protected int walign4(RandomAccessFile ttf) throws IOException {
		long fp = ttf.getFilePointer();
		long diff = ( (fp+3)/4 )*4 - fp;
		for (int i=0; i< diff; i++) {
			ttf.writeByte(0);
		}
		return (int)(fp+diff);
	}
	
	public static int getPos(String name) {
		for (int i=0; i < optOrder.size(); i++) {
			if (optOrder.get(i).compareToIgnoreCase(name)==0)
				return i;
		}
		return Integer.MAX_VALUE;
	}
}
