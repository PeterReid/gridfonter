package pl.edu.pw.mini.jozwickij.ttfedit.tables.common;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_nameRecord;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class TTFTable_name extends TTFTable {	
	
	public final static int FAMILY_ID = 1;
	public final static int VERSION_ID = 5;
	public static final int PSNAME_ID = 6;
	public final static int NAME_ID = 4;
	public final static int PRODUCER_ID = 8;
	public final static int STRPAD = 8;
		
	public int format = 0; //Set to 0.
	public int count = -1; // The number of nameRecords in this name table.
	public int stringOffset = -1; // Offset in bytes to the beginning of the name character strings.
	public TTFTable_nameRecord[] nameRecords = null; // The name records array[count].
	
	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String,TTFTable> tables) throws Exception {
		format = ttf.readUnsignedShort();
		count = ttf.readUnsignedShort();
		stringOffset = ttf.readUnsignedShort();
		nameRecords = new TTFTable_nameRecord[count];
		int msIDs = 0, macIDs = 0, uniIDs = 0;
		for (int i=0; i<count; i++) {
			nameRecords[i] = new TTFTable_nameRecord(ttf, offset+stringOffset);	
			switch (nameRecords[i].platformID) {
			case TTFTable_nameRecord.PLATID_MACINTOSH:
				macIDs++;
				break;
			case TTFTable_nameRecord.PLATID_MICROSOFT:
				msIDs++;
				break;
			case TTFTable_nameRecord.PLATID_UNICODE:
				uniIDs++;
				break;
			case TTFTable_nameRecord.PLATID_RESERVED:
				uniIDs++;
				break;
			}
		}
		this.fixNames(msIDs, macIDs, uniIDs);
		return true;
	}

	private void fixNames(int ms, int mac, int uni) {
		int inject = nameRecords.length;
		if (ms==0 && uni==0 && mac>0) {
			nameRecords = Util.makeRoom(TTFTable_nameRecord.class, nameRecords, inject, mac, null);
			for (int i=0; i < inject; i++) {
				nameRecords[inject+i] = (TTFTable_nameRecord) nameRecords[i].clone();
				nameRecords[inject+i].platformID = TTFTable_nameRecord.PLATID_MICROSOFT;
				nameRecords[inject+i].platformSpecificID = TTFTable_nameRecord.PLATSID_MICROSOFT;
				nameRecords[inject+i].languageID = 0x409;
			}
			count+=mac;
		}
		else if (mac==0 && uni==0 && ms>0) {
			nameRecords = Util.makeRoom(TTFTable_nameRecord.class, nameRecords, inject, ms, null);
			for (int i=0; i < inject; i++) {
				nameRecords[inject+i] = (TTFTable_nameRecord) nameRecords[i].clone();
				nameRecords[inject+i].platformID = TTFTable_nameRecord.PLATID_MACINTOSH;
				nameRecords[inject+i].platformSpecificID = 0;
				nameRecords[inject+i].languageID = 0;
			}
			count+=ms;
		}
	}
	
	@Override
	public JComponent getView(final Component c, Font f) {
		if (view==null) {
			setupView();
			view.setLayout(new GridLayout(0,1));
			for (int i=0; i<count; i++) {
				final JTextField jtf = new JTextField( nameRecords[i].name );
				JLabel lbl = new JLabel( nameRecords[i].getDescription() );
				nameRecords[i].assignTextField(jtf);
				if (nameRecords[i].nameID == NAME_ID) {
					jtf.addKeyListener(new KeyListener() {
						public void keyTyped(KeyEvent e) {
							((JFrame)c).setTitle(jtf.getText());							
						}
						public void keyPressed(KeyEvent e) {}
						public void keyReleased(KeyEvent e) {}
					}); 
				}
				view.add(lbl);
				view.add(jtf);				
			}			
		}
		return spanel;
	}

	public String getDescription() {
		String desc = "Missing font name";
		for (int i=0; i<count; i++) {
			if (nameRecords[i].nameID == NAME_ID) {
				return nameRecords[i].name;
			}
		}
		return desc;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		this.prepareWrite(ttf);
		ttf.writeShort(this.format);
		ttf.writeShort(this.count);
		ttf.writeShort(this.stringOffset=(count*12+8));
		if (count > 0)
			Arrays.sort(nameRecords);
		int offsetName = 0;
		for (int i=0; i<count; i++) {
			offsetName = nameRecords[i].write(ttf, my_offset+stringOffset, offsetName);			
		}
		ttf.seek(my_offset+stringOffset+offsetName);
		this.finishWrite(ttf);
		return true;
	}
	
	@Override
	public boolean isViewUserFriendly() {
		return true;
	}
}
