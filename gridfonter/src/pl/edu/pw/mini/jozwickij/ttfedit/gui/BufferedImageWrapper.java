package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class BufferedImageWrapper extends BufferedImage {
	
	private int imWidth  = 0;
	private int imHeight = 0;
	private static GraphicsDevice dev = null;
				
	public static int getScreenLen() {
		dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode dm = dev.getDefaultConfiguration().getDevice().getDisplayMode();
		return Math.max( dm.getWidth(), dm.getHeight() );
	}

	public BufferedImageWrapper(int width, int height, int imageType) {
		super(getScreenLen(), getScreenLen(), imageType);
		this.imWidth = width;
		this.imHeight = height;
	}

	public int getRealWidth() {
		return this.imWidth;
	}

	public int getRealHeight() {
		return this.imHeight;
	}	
}
