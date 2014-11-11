/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
	private static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
						: (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String execute(String text) {
		MessageDigest md;
		byte[] sha1hash = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		return convertToHex(sha1hash);
	}
}
