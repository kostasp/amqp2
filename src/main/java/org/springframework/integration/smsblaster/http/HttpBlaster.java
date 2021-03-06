package org.springframework.integration.smsblaster.http;


import com.google.common.io.Files;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.springframework.integration.service.SmsSender;
import org.springframework.integration.smsblaster.BlasterStatistics;
import org.springframework.integration.smsblaster.messaging.SMSFactory;
import org.springframework.integration.smsblaster.messaging.SMSFactoryUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpBlaster  implements SmsSender {
	public void sendMessages(String[] args) {
		HttpBlasterOptions opts = new HttpBlasterOptions();
		CmdLineParser parser = new CmdLineParser(opts);
		try {
			parser.parseArgument(args);
			if (opts.nthreads == null || opts.nthreads == 0) {
				opts.nthreads = opts.urls.size();
			}
			if (!opts.interactive && opts.nhits == null) {
				System.err.println("Must specify maximum hits for non-interactive use");
				System.exit(2);
			}
			if (opts.body != null) {
				try {
					String content = Files.toString(opts.body, Charset.forName("UTF-8"));
					opts.args = Arrays.asList(content);
				} catch (IOException e) {
					System.err.println("Error: failed to read file " + opts.body.getAbsolutePath());
					System.exit(2);
				}
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("java -jar smsblaster.jar [options...]");
			parser.printUsage(System.err);
			return;
		}
		
		SMSFactory moFactory = SMSFactoryUtils.getModule(opts.module, opts.args);
		if (moFactory == null) {
			System.err.printf("Failed to initialize messaging module%n");
			System.exit(2);
		}

		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
		connManager.setMaxTotal(4 * opts.nthreads);
		connManager.setDefaultMaxPerRoute(2 * opts.nthreads);
		DefaultHttpClient httpClient = new DefaultHttpClient(connManager);
		
		BlasterStatistics stats = new BlasterStatistics();
		final CyclicBarrier barrier = new CyclicBarrier(opts.nthreads + 1);
		ExecutorService exec = Executors.newFixedThreadPool(opts.nthreads);
		for (int i = 0; i < opts.nthreads; i++) {
			exec.execute(new HttpBlasterRunnable(httpClient, moFactory, opts, stats, barrier));
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
					System.out.printf("\tQPS %f%n", stats.getQPS());
				} else if ("q".equalsIgnoreCase(line)) {
					exec.shutdown();
					System.exit(2);
				} else if ("connections".equalsIgnoreCase(line)) {
					System.out.printf("\tActive Connections: %d%n", connManager.getConnectionsInPool());
				} else if ("stats".equalsIgnoreCase(line)) {
					System.out.printf("\tStatistics: (OK)%d (FAIL)%d%n", stats.getHitsSucceeded(), stats.getHitsFailed());
				} else {
					System.err.printf("\ti don't understand '%s'%n", line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
