package org.springframework.integration.smsblaster.messaging;

import java.util.Map;

public interface SMSFactory {
	
    SmsMessage newSmsObject();

    Map<String, String> newSMS();
    
    String newSMSAsString();

    byte[] newSMSAsBytes();
	
	Map<String, String> useContentHeaders();
	
	boolean sendAsForm();
}
