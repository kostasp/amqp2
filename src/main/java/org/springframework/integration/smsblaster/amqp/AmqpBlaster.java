package org.springframework.integration.smsblaster.amqp;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.integration.mongo.repositories.SmsMessageRepository;
import org.springframework.integration.service.SmsSender;
import org.springframework.integration.smsblaster.BlasterStatistics;
import org.springframework.integration.smsblaster.messaging.SMSFactory;
import org.springframework.integration.smsblaster.messaging.SMSFactoryUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/12/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class AmqpBlaster implements SmsSender {




    private MongoTemplate mongoTemplate;
    private AmqpTemplate amqpTemplate;
    private SmsMessageRepository smsMessageRepository;

    public void sendMessages(String[] args) {
        AmqpBlasterOptions opts = new AmqpBlasterOptions();
        CmdLineParser parser = new CmdLineParser(opts);
        try {
            parser.parseArgument(args);
            if (opts.nthreads == null || opts.nthreads == 0) {
                opts.nthreads = 1;
            }
            if (!opts.interactive && opts.nhits == null) {
                System.err.println("Must specify maximum hits for non-interactive use");
                System.exit(2);
            }

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar smsblaster.jar [options...]");
            parser.printUsage(System.err);
            return;
        }

        SMSFactory moFactory = SMSFactoryUtils.getModule("PostJson", opts.args);
        if (moFactory == null) {
            System.err.printf("Failed to initialize messaging module%n");
            System.exit(2);
        }
        BlasterStatistics stats = new BlasterStatistics();
        // delete everything from SMS store before running the test
        getSmsMessageRepository().deleteAll();

        ExecutorService exec = Executors.newFixedThreadPool(opts.nthreads);
        final CyclicBarrier barrier = new CyclicBarrier(opts.nthreads + 1);
        for (int i = 0; i < opts.nthreads; i++) {
            exec.execute(new AmqpBlasterRunnable(amqpTemplate, moFactory, opts, stats, barrier));
        }

        if (!opts.interactive) {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                System.err.printf("Barrier InterruptedException%n");
                System.exit(2);
            } catch (BrokenBarrierException e) {
                System.err.printf("Barrier BrokenBarrierException%n");
                System.exit(2);
            }
            System.out.printf("Hits: %d Errors: %d Time: %f sec Threads: %d QPS: %f%n",
                    stats.getHitsSucceeded(), stats.getHitsFailed(), stats.lastDurationInSecs(), opts.nthreads, stats.getQPS());
            System.exit(0);
        }


        BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            while ((line = bin.readLine()) != null) {
                if ("qps".equalsIgnoreCase(line) || line.isEmpty()) {
                    long messsagesInDB = getSmsMessageRepository().count();
                    stats.setTotalHitsSucceeded(messsagesInDB);
                    System.out.printf("\tQPS %f%n", stats.getQPS());
                } else if ("q".equalsIgnoreCase(line)) {
                    exec.shutdown();
                    System.exit(2);
                } else if ("rate".equalsIgnoreCase(line)) {
                    long messsagesInDB = getSmsMessageRepository().count();
                    long messagesSendToRabbit = stats.getTotalHitsSend();
                    stats.setTotalHitsSucceeded(messsagesInDB);
                    System.out.printf("\tMessages sent: (OK)%d Messages stored:%d%n", messagesSendToRabbit, messsagesInDB);
                } else if ("stats".equalsIgnoreCase(line)) {
                    long messsagesInDB = getSmsMessageRepository().count();
                    stats.setTotalHitsSucceeded(messsagesInDB);
                    System.out.printf("\tStatistics: (OK)%d (FAIL)%d%n", stats.getHitsSucceeded(), stats.getHitsFailed());
                } else {
                    System.err.printf("\ti don't understand '%s'%n", line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setSmsMessageRepository(SmsMessageRepository smsMessageRepository) {
        this.smsMessageRepository = smsMessageRepository;
    }

    public SmsMessageRepository getSmsMessageRepository() {
        return smsMessageRepository;
    }
}
