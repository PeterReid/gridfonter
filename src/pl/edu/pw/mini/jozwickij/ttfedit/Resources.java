package pl.edu.pw.mini.jozwickij.ttfedit;

import java.io.File;
import java.net.URL;

public class Resources {

	public static URL get(Class clazz, String file) {
		URL url = null;
		try {
			File f = new File(file);
			if (f.canRead())
				url = f.toURL();
		}
		catch (Exception e) {}
		return url;
	}

}
