package org.springframework.integration.smsblaster.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.integration.smsblaster.messaging.SMS;
import org.springframework.integration.smsblaster.messaging.SMSFactory;
import org.springframework.integration.smsblaster.messaging.SmsMessage;

import java.sql.Timestamp;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/12/12
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractObjectMessageFactory implements SMSFactory{
    private static Gson gson;


    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    public byte[] newSMSAsBytes() {
        return gson.toJson(newSMS()).getBytes();
    }

    @Override
    public String newSMSAsString() {
        return gson.toJson(newSMS());
    }

    @Override
    public SmsMessage newSmsObject() {
        SmsMessage sms= new SmsMessage();
        sms.setAccountId("19823");
        sms.setCode("46541");
        sms.setOperatorId(new SMS().any(Arrays.asList("383")));
        sms.setMessageContent(new SMS().any(Arrays.asList("Hello", "Ciao", "Salut", "Hola")));
        sms.setOriginatingAddress(new SMS().any(Arrays.asList("90000", "90001", "90002")));
        sms.setMsisdn(new SMS().any(Arrays.asList("693456180", "+698456", "+5564866")));
        String uniqueId=new SMS().uniqueMessageId("sy");
        sms.setUniqueId(uniqueId);
        sms.setReceivedServiceNumber("19383");
        sms.setMcc("22");
        sms.setMnc("10");
        sms.setTimeStamp(new Timestamp(System.currentTimeMillis()));
        return sms;
    }

}
