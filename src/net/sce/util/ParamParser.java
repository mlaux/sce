package net.sce.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamParser {
	private static final String url = "http://world1.runescape.com/plugin.js?param=o0,a0,b0";
	private static Map<String, String> parameters;
	
	private static void load() {
		parameters = new HashMap<String, String>();
		String html = "", line;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((line = br.readLine()) != null) {
				html += line;
			}
			Pattern regex = Pattern.compile("<param name=([^\\s]+)\\s+value=([^>]*)>", Pattern.CASE_INSENSITIVE);
			Matcher regexMatcher = regex.matcher(html);
			while (regexMatcher.find())
				if (!parameters.containsKey(regexMatcher.group(1)))
					parameters.put(regexMatcher.group(1).replace("\"", ""), regexMatcher.group(2).replace("\"", ""));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key) { if(parameters == null) load(); return parameters.get(key); }
}
