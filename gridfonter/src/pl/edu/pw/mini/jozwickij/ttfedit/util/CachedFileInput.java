package pl.edu.pw.mini.jozwickij.ttfedit.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CachedFileInput implements RandomAccessInput {
	
	private byte[] body = null;
	private long fp = 0;
	private ByteArrayInputStream bais = null;
	private DataInputStream dis = null;

	public CachedFileInput(RandomAccessFile raf, int offset) throws IOException {
		this.body = new byte[(int) raf.length()];
		raf.seek(0);
		Debug.println("Reading file into memory...",this);
		raf.readFully(body);
		this.bais = new ByteArrayInputStream(this.body);
		this.dis = new DataInputStream(bais);
		dis.mark(dis.available());
		this.seek(offset);
	}
	
	public void close() {
		try {
			dis.close();
		}
		catch (Exception e) {}
		try {
			bais.close();
		}
		catch (Exception e) {}
		body = null;
		dis = null;
		bais = null;
	}
	
	public long getFilePointer() {
		return fp;
	}
	
	public void seek(long where) throws IOException {
		dis.reset();
		fp = dis.skip(where);		
	}
	
	public int skipBytes(int count) throws IOException {
		int n = dis.skipBytes(count);
		fp+=n;
		return n;
	}
	
	public byte readByte() throws IOException {
		fp+=1;
		return dis.readByte();
	}
	
	public int readUnsignedByte() throws IOException {
		fp+=1;
		return dis.readUnsignedByte();
	}
	
	public short readShort() throws IOException {
		fp+=2;
		return dis.readShort();
	}
	
	public int readUnsignedShort() throws IOException {
		fp+=2;
		return dis.readUnsignedShort();
	}
	
	public long readLong() throws IOException {
		fp+=8;
		return dis.readLong();
	}
	
	public int readInt() throws IOException {
		fp+=4;
		return dis.readInt();
	}
	
	public byte read() throws IOException {
		fp+=1;
		return dis.readByte();
	}
	
	public int read(byte[] buff) throws IOException {
		int n = dis.read(buff);
		fp+=n;
		return n;
	}
	
	public int read(byte[] buff, int off, int len) throws IOException {
		int n = dis.read(buff,off,len);
		fp+=n;
		return n;
	}
	
	public void readFully(byte[] buff) throws IOException {
		dis.readFully(buff);
		fp+=buff.length;
	}
	
	public void readFully(byte[] buff, int off, int len) throws IOException {
		dis.readFully(buff,off,len);
		fp+= off+len;
	}

	public boolean readBoolean() throws IOException {
		fp+=1;
		return dis.readBoolean();
	}

	public char readChar() throws IOException {
		fp+=2;
		return dis.readChar();
	}

	public float readFloat() throws IOException {
		fp+=2;
		return dis.readFloat();
	}

	public double readDouble() throws IOException {
		fp+=4;
		return dis.readDouble();
	}

	public String readLine() throws IOException {
		throw new IOException("Not implemented!");
	}

	public String readUTF() throws IOException {
		throw new IOException("Not implemented!");
	}
	
}
