package org.springframework.integration.smsblaster.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.integration.smsblaster.GenericOptions;
import org.springframework.integration.smsblaster.http.HttpBlasterOptions;
import org.springframework.integration.smsblaster.BlasterStatistics;
import org.springframework.integration.smsblaster.messaging.SMSFactory;
import org.springframework.integration.smsblaster.modules.GetQuery;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/12/12
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class AmqpBlasterRunnable implements Runnable {

    private AmqpTemplate amqpTemplate;
    private GenericOptions opts;
    private BlasterStatistics stats;
    private SMSFactory fact;
    private CyclicBarrier barrier;

    public AmqpBlasterRunnable(AmqpTemplate template, SMSFactory fact,
                               GenericOptions opts, BlasterStatistics stats, CyclicBarrier barrier) {
        this.amqpTemplate = template;
        this.fact = fact;
        this.opts = opts;
        this.stats = stats;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        boolean hitForEver = opts.nhits == null;

        if (hitForEver) {
            for (; ; ) doSend();
        } else {
            while (getHitsSoFar() < opts.nhits) doSend();
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            System.err.printf("Barrier InterruptedException%n");
        } catch (BrokenBarrierException e) {
            System.err.printf("Barrier BrokenBarrierException%n");
        }

    }

    private void doSend() {
        try {
            amqpTemplate.convertAndSend(fact.newSmsObject());
            stats.noteHitMade();
        } catch (AmqpException ex) {
            stats.noteHitNotMade();
        }
    }

    public long getHitsSoFar() {
        return stats.getTotalHitsSend();
    }

}
