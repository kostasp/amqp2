package org.springframework.integration.mongo.repositories;

import org.springframework.integration.smsblaster.messaging.SmsMessage;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/18/12
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SmsMessageCustomRepository extends SmsMessageRepository {
    SmsMessage findByMsisdn(String msisdn) ;
}
