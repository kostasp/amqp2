package org.springframework.integration.smsblaster;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class BlasterStatistics {
    private ConcurrentMap<URL, AtomicLong> hitsSucceeded =
            new ConcurrentHashMap<URL, AtomicLong>();
    private AtomicLong totalHitsSend = new AtomicLong(0);
    private AtomicLong totalHitsNotSend= new AtomicLong(0);
    private AtomicLong totalHitsSucceeded = new AtomicLong(0);
    private ConcurrentMap<URL, AtomicLong> hitsFailed =
            new ConcurrentHashMap<URL, AtomicLong>();
    private AtomicLong totalHitsFailed = new AtomicLong(0);

    public void noteHitMade() {
        totalHitsSend.addAndGet(1L);
    }

    public void noteHitNotMade() {
        totalHitsNotSend.addAndGet(1L);
    }

    public void noteSuccess() {
        totalHitsSucceeded.addAndGet(1L);
    }

    public void noteSuccess(URL u) {
        AtomicLong hitsOfURL = hitsSucceeded.putIfAbsent(u, new AtomicLong(1));
        if (hitsOfURL != null) {
            hitsOfURL.addAndGet(1L);
        }
        totalHitsSucceeded.addAndGet(1L);
    }

    public void noteFailure() {
        totalHitsFailed.addAndGet(1L);
    }

    public void noteFailure(URL u) {
        AtomicLong hitsOfURL = hitsFailed.putIfAbsent(u, new AtomicLong(1));
        if (hitsOfURL != null) {
            hitsOfURL.addAndGet(1L);
        }
        totalHitsFailed.addAndGet(1L);
    }

    public Long getHitsFailed() {
        return totalHitsFailed.get();
    }

    public Long getHitsSucceeded() {
        return totalHitsSucceeded.get();
    }

    public Long getHitsTotal() {
        return getHitsFailed() + getHitsSucceeded();
    }

    private long lastPeekHits = 0;
    private Date formerPeekTime = new Date();
    private Date lastPeekTime = new Date();

    public double getQPS() {
        Date now = new Date();
        long currHits = getHitsTotal();
        long hitsDiff = currHits - lastPeekHits;
        double timeDiff = (now.getTime() - lastPeekTime.getTime()) / 1000.0;
        lastPeekHits = currHits;
        formerPeekTime = lastPeekTime;
        lastPeekTime = now;
        return hitsDiff / timeDiff;
    }

    public double lastDurationInSecs() {
        return (lastPeekTime.getTime() - formerPeekTime.getTime()) / 1000.0;
    }

    public Long getTotalHitsSend() {
        return totalHitsSend.get();
    }

    public Long getTotalHitsNotSend() {
        return totalHitsNotSend.get();
    }
    
    public void setTotalHitsSucceeded(Long hitsSucceeded){
        totalHitsSucceeded.addAndGet(hitsSucceeded);
    }
}
