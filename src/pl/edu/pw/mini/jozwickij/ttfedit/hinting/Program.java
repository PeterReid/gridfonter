package pl.edu.pw.mini.jozwickij.ttfedit.hinting;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Program {
	public final static short PUSHW    = 0xb8;
	public final static short SCANCTRL = 0x85;
	public final static short SCANTYPE = 0x8d;
	public final static short DO_CTR_ALWAYS  = 0x1ff;
	public final static short SIMPLE_DO_CTRL = 0x00;
	public final static short SMART_DO_CTRL  = 0x04;	//MSFT extension
	
	private static byte[] hintProg = null;
	private final static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private final static DataOutputStream dataOut = new DataOutputStream(baos);
	
	static {
		try {
			dataOut.writeByte(PUSHW);
			dataOut.writeShort(SIMPLE_DO_CTRL);
			dataOut.writeByte(SCANTYPE);
			
			dataOut.writeByte(PUSHW);
			dataOut.writeShort(SMART_DO_CTRL);
			dataOut.writeByte(SCANTYPE);
			
			dataOut.writeByte(PUSHW);
			dataOut.writeShort(DO_CTR_ALWAYS);
			dataOut.writeByte(SCANCTRL);
			
			dataOut.close();
			baos.close();
			hintProg = baos.toByteArray();
		}
		catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static byte[] getInstructions() {
		return hintProg.clone();
	}

	public static int getInstructionsCount() {
		return hintProg.length;
	}	
}
