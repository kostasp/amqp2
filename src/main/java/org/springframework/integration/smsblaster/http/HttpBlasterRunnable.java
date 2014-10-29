package org.springframework.integration.smsblaster.http;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.integration.smsblaster.BlasterStatistics;
import org.springframework.integration.smsblaster.messaging.SMSFactory;
import org.springframework.integration.smsblaster.modules.GetQuery;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class HttpBlasterRunnable implements Runnable {
	private Random rnd = new Random();
	private DefaultHttpClient httpClient;
	private SMSFactory fact;
	private HttpBlasterOptions opts;
	private BlasterStatistics stats;
	private CyclicBarrier barrier;
	
	public HttpBlasterRunnable(DefaultHttpClient httpClient, SMSFactory fact,
                               HttpBlasterOptions opts, BlasterStatistics stats, CyclicBarrier barrier) {
		this.httpClient = httpClient;
		this.fact = fact;
		this.opts = opts;
		this.stats = stats;
		this.barrier = barrier;
	}
	
	public long getHitsSoFar() {
		return opts.countErrors? stats.getHitsTotal(): stats.getHitsSucceeded();
	}
	
	private boolean hitPost() {
		URL u = opts.urls.get(rnd.nextInt(opts.urls.size()));
		try {
			HttpPost req = new HttpPost(u.toString());
			for (Map.Entry<String, String> h : fact.useContentHeaders().entrySet()) {
				req.addHeader(h.getKey(), h.getValue());
			}
			HttpEntity reqEntity = null;
			Map<String, String> mo = fact.newSMS();
			if (fact.sendAsForm()) {
				List<NameValuePair> qparams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> val : mo.entrySet()) {
					qparams.add(new BasicNameValuePair(val.getKey(), val.getValue()));
				}
				reqEntity = new UrlEncodedFormEntity(qparams, "UTF-8");
			} else {
				reqEntity = new StringEntity(mo.entrySet().iterator().next().getValue());
			}
			req.setEntity(reqEntity);
			HttpResponse resp = httpClient.execute(req);
			StatusLine line = resp.getStatusLine();
			String content = null;
			HttpEntity rent = resp.getEntity();
			if (rent != null) {
				content = EntityUtils.toString(rent, "UTF-8");
			}
			if (line.getStatusCode() / 100 != 2) {
				System.err.printf("hitPost return code %d: %s%n", line.getStatusCode(), content);
				stats.noteFailure(u);
				return false;
			}
			stats.noteSuccess(u);
			return true;
		} catch (Exception e) {
			System.err.println("hitPost error: " + e.getMessage());
			stats.noteFailure(u);
			return false;
		}
	}
	
	private boolean hitGet() {
		URL u = opts.urls.get(rnd.nextInt(opts.urls.size()));
		try {
			HttpGet req = null;
			Map<String, String> mo = fact.newSMS();
			if (!mo.isEmpty() && fact.sendAsForm()) {
				List<NameValuePair> qparams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> val : mo.entrySet()) {
					qparams.add(new BasicNameValuePair(val.getKey(), val.getValue()));
				}
				URI uri = URIUtils.createURI(u.getProtocol(), u.getHost(), u.getPort(), u.getFile(), URLEncodedUtils.format(qparams, "UTF-8"), null);
				req = new HttpGet(uri);
			} else {
                UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(u.toString());
				req = new HttpGet(urlBuilder.build().encode().toUriString());
			}
			for (Map.Entry<String, String> h : fact.useContentHeaders().entrySet()) {
				req.addHeader(h.getKey(), h.getValue());
			}
			HttpResponse resp = httpClient.execute(req);
			StatusLine line = resp.getStatusLine();
			String content = null;
			HttpEntity rent = resp.getEntity();
			if (rent != null) {
				content = EntityUtils.toString(rent, "UTF-8");
			}
			if (line.getStatusCode() / 100 != 2) {
				System.err.printf("hitGet return code %d: %s%n", line.getStatusCode(), content);
				stats.noteFailure(u);
				return false;
			}
			stats.noteSuccess(u);
			return true;
		} catch (Exception e) {
			System.err.println("hitGet error: " + e.getMessage());
			stats.noteFailure(u);
			return false;
		}
	}
	
	@Override
	public void run() {
		boolean hitForEver = opts.nhits == null;
		boolean doGet = fact instanceof GetQuery;
		if (doGet) {
			if (hitForEver) {
				for(;;) hitGet();
			} else {
				while (getHitsSoFar() < opts.nhits) hitGet();
			}
		} else {
			if (hitForEver) {
				for(;;) hitPost();
			} else {
				while (getHitsSoFar() < opts.nhits) hitPost();
			}
		}
		try {
			barrier.await();
		} catch (InterruptedException e) {
			System.err.printf("Barrier InterruptedException%n");
		} catch (BrokenBarrierException e) {
			System.err.printf("Barrier BrokenBarrierException%n");
		}
	}
}
