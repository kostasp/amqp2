package org.springframework.integration.smsblaster.messaging;

import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/17/12
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmsMessage extends SMS{
    
    private String msisdn;
    private String code;
    private String originatingAddress;
    private String messageContent;
    private String operatorId;
    private String accountId;
    //The unique mongo id( replacing the auto generated _id)
    @Id
    private String uniqueId;
    private String mcc;
    private String mnc;
    private String receivedServiceNumber;
    private Timestamp timeStamp;
    private String keyword;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginatingAddress() {
        return originatingAddress;
    }

    public void setOriginatingAddress(String originatingAddress) {
        this.originatingAddress = originatingAddress;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getReceivedServiceNumber() {
        return receivedServiceNumber;
    }

    public void setReceivedServiceNumber(String receivedServiceNumber) {
        this.receivedServiceNumber = receivedServiceNumber;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
