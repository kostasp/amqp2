package org.springframework.integration.smsblaster.messaging;

import java.text.SimpleDateFormat;
import java.util.*;

public class SMS {
	private Random rnd = new Random();
	public static final Map<String, Object> forMvel;
	static {
		forMvel = new HashMap<String, Object>();
		forMvel.put("SMS", new SMS());
	}
    
	public <T> T any(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(rnd.nextInt(list.size()));
	}
	
	public String now(String fmt) {
		SimpleDateFormat f = new SimpleDateFormat(fmt);
		return f.format(new Date());
	}
	
	public String randomUUID() {
		UUID uuid= UUID.randomUUID();
        return uuid.toString();
	}
	
	private static final String DIGITS = "0123456789";
	
	// needed because long cannot store a 15digits msisdn
	private String randomNumber(int len) {
		if (len <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(DIGITS.charAt(rnd.nextInt(DIGITS.length())));
		}
		return sb.toString();
	}
	
	public String randomMSISDN(String prefix) {
		if (prefix == null) {
			return randomNumber(15);
		}
		return prefix + randomNumber(15 - prefix.length());
	}
	
	public String uniqueMessageId(String prefix) {
		Long tid = Thread.currentThread().getId();
		Long now = System.currentTimeMillis();
		return String.format("%s:%d:%d%s", prefix, tid, now,UUID.randomUUID().toString());
	}
}
