package org.springframework.integration.mongo.repositories;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/18/12
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SmsMessageLiteRepository extends CrudRepository<String,String> {
}
