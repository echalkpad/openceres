package com.openceres.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class StringUtils {
	public static String setLastSlash(String path) {
		if (path.charAt(path.length() - 1) == '/')
			return path;
		else
			return path + "/";
	}

	public static String deleteLastToken(final String src, String deleteToken) {
		String removedString = src;
		if (src.endsWith(deleteToken)) {
			removedString = src.substring(0, src.length() - 1);
		}

		return removedString;
	}

	public static List<String> parseString2(String src, String delimeter) {
		List<String> result = new ArrayList<String>();
		String tempString;

		int index = 0, pos;

		pos = src.indexOf(delimeter);
		while (pos > 0) {
			tempString = src.substring(index, pos + delimeter.length());
			src = src.substring(pos + delimeter.length());
			result.add(tempString);
			pos = src.indexOf(delimeter);
		}
		;

		return result;
	}

	public static List<String> parseString(String src, String delimeter) {
		List<String> result = new ArrayList<String>();
		String tempString;

		StringTokenizer token = new StringTokenizer(src, delimeter);
		while (token.hasMoreTokens()) {
			tempString = token.nextToken();
			if (tempString.length() > 0) {
				result.add(tempString);
			}
		}

		return result;
	}

	public static String toLowerFirstCh(String param) {
		if (!isEmpty(param)) {
			return param.substring(0, 1).toLowerCase() + param.substring(1, param.length());
		}

		return null;
	}

	public static String toUpperFirstCh(String param) {
		if (!isEmpty(param)) {
			return param.substring(0, 1).toUpperCase() + param.substring(1, param.length());
		}

		return null;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static String getCharacters(int seed) {
		char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
				'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z' };

		Random rand = new Random();
		String str = "";

		StringBuffer sb = new StringBuffer(128);

		for (int j = 0; j < seed; j++) {
			for (int k = 0; k < 1; k++) {
				sb.append(ch[rand.nextInt(36)]);
			}
		}

		str = sb.toString();
		return str;
	}

	/**
	 * Returns an arraylist of strings.
	 * 
	 * @param str the comma seperated string values
	 * @return the arraylist of the comma seperated string values
	 */
	public static String[] getStrings(String str) {
		Collection<String> values = getStringCollection(str);
		if (values.size() == 0) {
			return null;
		}
		return values.toArray(new String[values.size()]);
	}

	/**
	 * Returns a collection of strings.
	 * 
	 * @param str comma seperated string values
	 * @return an <code>ArrayList</code> of string values
	 */
	public static Collection<String> getStringCollection(String str) {
		List<String> values = new ArrayList<String>();
		if (str == null)
			return values;
		StringTokenizer tokenizer = new StringTokenizer(str, ",");
		values = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			values.add(tokenizer.nextToken());
		}
		return values;
	}

	/**
	 * Given an array of strings, return a comma-separated list of its elements.
	 * 
	 * @param strs Array of strings
	 * @return Empty string if strs.length is 0, comma separated list of strings otherwise
	 */
	public static String arrayToString(String[] strs) {
		if (strs.length == 0) {
			return "";
		}
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(strs[0]);
		for (int idx = 1; idx < strs.length; idx++) {
			sbuf.append(",");
			sbuf.append(strs[idx]);
		}
		return sbuf.toString();
	}
}
