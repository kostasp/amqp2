package org.springframework.integration.service;

import org.springframework.expression.spel.support.ReflectionHelper;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/12/12
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SmsSender {
    
    void sendMessages(String[] args);
}
