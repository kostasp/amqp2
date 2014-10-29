package org.springframework.integration.mongo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.integration.smsblaster.messaging.SmsMessage;

/**
 * Repository used for SMS
 */
public interface SmsMessageRepository extends CrudRepository<SmsMessage,String>{

}
