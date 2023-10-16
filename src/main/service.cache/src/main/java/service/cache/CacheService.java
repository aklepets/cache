package service.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private static final int MAX_SIZE = 100_000;
    private static final long EVICTION_TIME_MS = 5_000;
    private final Map<CacheEntry, Long> cache;
    private long totalPutTimeMilliSeconds;
    private long evictionCount;

    public CacheService() {
        cache = new ConcurrentHashMap<>(MAX_SIZE);
        totalPutTimeMilliSeconds = 0;
        evictionCount = 0;
    }

    public void put(CacheEntry c){
        long startTime = System.currentTimeMillis();
        if (cache.size() == MAX_SIZE) {
            evictOldestEntry();
        }
        evictOutdatedEntries();
        cache.put(c, System.currentTimeMillis());
        totalPutTimeMilliSeconds += System.currentTimeMillis() - startTime;
        System.out.println("cache size is: " + cache.size());
        System.out.println("cache totalPutTime: " + totalPutTimeMilliSeconds / 1_000_000);
    }

    public Long get(CacheEntry c){
        if (cache.containsKey(c)){
            if (System.currentTimeMillis() - cache.get(c) < EVICTION_TIME_MS)
            {
                return cache.get(c);
            } else {
                evictOutdatedEntries();
            }
        }
        return null;
    }

    private void evictOldestEntry(){
        CacheEntry oldestElement = null;
        long oldestTimestamp = Long.MAX_VALUE;

        for (Map.Entry<CacheEntry, Long> entry : cache.entrySet()) {
            long timestamp = entry.getValue();
            if (timestamp < oldestTimestamp) {
                oldestTimestamp = timestamp;
                oldestElement = entry.getKey();
            }
        }
        cache.entrySet().remove(oldestElement);
        evictionCount++;
    }

    private void evictOutdatedEntries(){
        cache.entrySet().removeIf(entry -> {
            boolean isEvicted = System.currentTimeMillis() - entry.getValue() > EVICTION_TIME_MS;
            if (isEvicted) {evictionCount++;}
            return isEvicted;
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
            cacheService.put(new CacheEntry("item" + i) );
        }

        cacheService.cache.entrySet().forEach(element -> element.toString());

        long value = cacheService.get(new CacheEntry("item1"));
        System.out.println("Value for key item1 is " + value);

        cacheService.printStatistics();
    }
}
