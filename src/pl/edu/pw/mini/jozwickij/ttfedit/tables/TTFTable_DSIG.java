package pl.edu.pw.mini.jozwickij.ttfedit.tables;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Map;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;

public class TTFTable_DSIG extends TTFTable {
	
	private final static long UINT_MASK = (1L << 32)-1L;
	protected int ulVersion	= 0x00000001;			// Version number of the DSIG table (0x00000001)
	protected int usNumSigs	= 1;					// Number of signatures in the table
	protected int usFlag	= 0;					// permission flags	Bit 0: cannot be resigned, Bits 1-7: Reserved (Set to 0)
	protected int ulFormat	= 1;					// format of the signature
	protected int ulLength	= 0;					// Length of signature in bytes
	protected int ulOffset	= 20;					// Offset to the signature block from the beginning of the table
	
	protected int usReserved1 = 0;					// Reserved for later use; 0 for now
	protected int usReserved2 = 0;					// Reserved for later use; 0 for now
	protected int cbSignature = 0;					// Length (in bytes) of the PKCS#7 packet in pbSignature
	private byte[] bSignature = null;				// PKCS#7 packet
	
	protected byte[] digest = null;

	@Override
	public boolean readFrom(RandomAccessFile ttf, int offset, int length, int checksum, Map<String, TTFTable> tables) throws Exception {
		this.ulVersion = (int) (ttf.readInt() & UINT_MASK);
		this.usNumSigs = ttf.readUnsignedShort();
		this.usFlag = ttf.readUnsignedShort();
		this.ulFormat = (int) (ttf.readInt() & UINT_MASK);
		this.ulLength = (int) (ttf.readInt() & UINT_MASK);
		this.ulOffset = (int) (ttf.readInt() & UINT_MASK);
		ttf.seek(offset + ulOffset);
		this.usReserved1 = ttf.readUnsignedShort();
		this.usReserved2 = ttf.readUnsignedShort();
		this.cbSignature = (int) (ttf.readInt() & UINT_MASK);
		bSignature = new byte[cbSignature];
		ttf.readFully(bSignature);
		return true;
	}
	
	@Override
	public boolean writeToFile(RandomAccessFile ttf, Map<String, TTFTable> tables) throws Exception {
		ttf.seek(0);
		byte[] body = new byte[(int) ttf.length()];
		ttf.readFully(body);
		MessageDigest sha = MessageDigest.getInstance("SHA1");
		sha.update(body);
		digest = sha.digest();
		return true;
	}	
}
