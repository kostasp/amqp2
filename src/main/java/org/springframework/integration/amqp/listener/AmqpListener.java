package org.springframework.integration.amqp.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.integration.mongo.repositories.SmsMessageRepository;
import org.springframework.integration.smsblaster.messaging.SmsMessage;

/**
 * Simple Amqp Listener that stores to mongo
 */
public class AmqpListener {




    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpListener.class);
    private MongoTemplate mongoTemplate;


    private SmsMessageRepository smsMessageRepository;

    public void handleMessage(SmsMessage sms) {
        try {
//            getMongoTemplate().save(sms);
            getSmsMessageRepository().save(sms);
        } catch (Exception e) {

            LOGGER.error("Failed to save messsage", e);
            throw new RuntimeException(e);
        }
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public SmsMessageRepository getSmsMessageRepository() {
        return smsMessageRepository;
    }

    public void setSmsMessageRepository(SmsMessageRepository smsMessageRepository) {
        this.smsMessageRepository = smsMessageRepository;
    }
}
