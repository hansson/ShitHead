package com.hansson.shithead.util;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

	// Crapilly crap crap with a big rap I no security engineer :<
	public static String getHash(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
			md.update(sb.toString().getBytes());
			digest = md.digest();
			sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// Should not be able to happen
			Log.e(Constants.LOG_TAG, "Invalid hash algoritm");
			return "";
		}
	}
}
