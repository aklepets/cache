package service.cache;

import java.util.EventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private static final int MAX_SIZE = 100;
    private static final long EVICTION_TIME_MS = 5_000;
    private Map<String, Long> cache;
    private long totalPutTimeMiliSeconds;
    private long evictionCount;

    EventListener eventListener = new EventListener() {
    };

    public CacheService() {
        cache = new ConcurrentHashMap<>(MAX_SIZE);
        totalPutTimeMiliSeconds = 0;
        evictionCount = 0;
    }

    public void put(String s){
//        evictEntries();
        long startTime = System.currentTimeMillis();
        cache.put(s, System.currentTimeMillis());
        totalPutTimeMiliSeconds += System.currentTimeMillis() - startTime;
        System.out.println("cache size is: " + cache.size());
        System.out.println("cache totalPutTime: " + totalPutTimeMiliSeconds / 1_000_000);
    }

    public Long get(String key){
        if (cache.keySet().contains(key)){
            return cache.get(key);
        }
        return null;
    }

    private void evictEntries(){
        cache.entrySet().removeIf(entry -> {
            boolean isEvicted = System.currentTimeMillis() - entry.getValue() > EVICTION_TIME_MS;
            if (isEvicted) {evictionCount++;}
            return isEvicted;
        });
    }



    public void printStatistics(){
        System.out.println("Cache statistics:");
        System.out.println("Cache size:" + cache.size());
        System.out.println("average time spent putting new values into cache: " + totalPutTimeMiliSeconds + " miliseconds");
        System.out.println("number of cache evictions: " + evictionCount);
    }

    public static void main(String[] args) throws InterruptedException{
        CacheService cacheService = new CacheService();

        for (int i = 0; i < 200; i++){
            Thread.sleep(100);
            cacheService.put("item" + i );
        }

        cacheService.cache.entrySet().stream().forEach(element -> element.toString());

        long value = cacheService.get("item1");
        System.out.println("Value for key item1 is " + value);

        cacheService.printStatistics();
    }
}
