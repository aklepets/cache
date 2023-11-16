package service.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private static final int MAX_SIZE = 100_000;
    private static final long EVICTION_TIME_MS = 5_000;
    private final Map<Integer, TimedCacheEntry> cache;
    private long totalPutTimeMilliSeconds;
    private long evictionCount;

    public CacheService() {
        cache = new ConcurrentHashMap<>(MAX_SIZE);
        totalPutTimeMilliSeconds = 0;
        evictionCount = 0;
    }

    public void put(int id, CacheEntry cacheEntry){
        long startTime = System.currentTimeMillis();
        if (cache.size() == MAX_SIZE) {
            evictOutdatedEntries();
        }
        TimedCacheEntry timedCacheEntry = new TimedCacheEntry(cacheEntry, startTime);
        cache.put(id, timedCacheEntry);
        totalPutTimeMilliSeconds += System.currentTimeMillis() - startTime;
        System.out.println("cache size is: " + cache.size());
        System.out.println("cache totalPutTime: " + totalPutTimeMilliSeconds / 1_000_000);
    }

    public CacheEntry get(int id){
        if (cache.containsKey(id)){
            if (System.currentTimeMillis() - cache.get(id).getTimestamp() < EVICTION_TIME_MS)
            {
                return cache.get(id).getEntry();
            } else {
                evictOutdatedEntries();
            }
        }
        return null;
    }

    private void evictOutdatedEntries(){
        cache.entrySet().removeIf(entry -> {
            boolean toEvict = System.currentTimeMillis() - entry.getValue().getTimestamp() > EVICTION_TIME_MS;
            if (toEvict) {evictionCount++;}
            return toEvict;
        });
    }

    public void printStatistics(){
        System.out.println("Cache statistics:");
        System.out.println("Cache size:" + cache.size());
        System.out.println("average time spent putting new values into cache: " + totalPutTimeMilliSeconds + " miliseconds");
        System.out.println("number of cache evictions: " + evictionCount);
    }

    public static void main(String[] args) throws InterruptedException{
        CacheService cacheService = new CacheService();

        for (int i = 0; i < 200; i++){
            cacheService.put(i, new CacheEntry("item" + i) );
        }

        cacheService.cache.entrySet().forEach(element -> element.toString());

        CacheEntry value = cacheService.get(123);
        System.out.println("Value for key item1 is " + value);

        cacheService.printStatistics();
    }

    private static class TimedCacheEntry {
        private final CacheEntry entry;
        private final Long timestamp;

        private TimedCacheEntry(CacheEntry entry, Long timestamp){
            this.entry = entry;

            this.timestamp = timestamp;
        }

        public CacheEntry getEntry() {
            return entry;
        }

        public Long getTimestamp() {
            return timestamp;
        }

    }
}
