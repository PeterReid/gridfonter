package pl.edu.pw.mini.jozwickij.ttfedit.util;

import java.io.DataInput;
import java.io.IOException;

public interface RandomAccessInput extends DataInput {
	public long getFilePointer();
	public void seek(long where) throws IOException;
	public void close();
}
