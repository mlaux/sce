package net.sce.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Downloads files.
 *
 * @Author RS-Hacking.com
 */
public class Downloader {
	public static File downloadFile(String url, String dest, boolean force) throws IOException {
		File file = new File(dest);
		if (file.exists() && !force) {
			System.out.println(" - JAR file already exists, using cached copy");
			return file;
		}
		InputStream in = new URL(url).openStream();
		FileOutputStream out = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		int i;
		while ((i = in.read(buf)) != -1)
			out.write(buf, 0, i);
		in.close();
		out.close();
		System.out.println(" - JAR file downloaded successfully");
		return file;
	}
}