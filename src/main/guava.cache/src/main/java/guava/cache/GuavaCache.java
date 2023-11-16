package guava.cache;

import com.google.common.cache.*;
import service.cache.CacheEntry;

import java.util.concurrent.TimeUnit;

public class GuavaCache {
    public static void main(String[] args) throws InterruptedException {

        RemovalListener<Integer, CacheEntry> listener = new RemovalListener<>() {
            int numberItemsEvicted = 0;
            @Override
            public void onRemoval(RemovalNotification<Integer, CacheEntry> n){
                if (n.wasEvicted()) {
                    numberItemsEvicted++;
                    Integer key = n.getKey();
                    CacheEntry value = n.getValue();
                    String cause = n.getCause().name();
                    System.out.println("key was: " + key + "value was: " + value + "cause was: "+ cause);
                }
                System.out.println("number of items evicted " + numberItemsEvicted);
            }
        };

        Cache<Integer, CacheEntry> cache = CacheBuilder
                .newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .expireAfterWrite(500, TimeUnit.SECONDS)
                .removalListener(listener)
                .build();

        long startTime = System.nanoTime();
        int itemCount = 200_000;
        for (int i = 0; i < itemCount; i++){
            cache.put(i, new CacheEntry("item" + i));
        }
        long endTime = System.nanoTime();
        double averageTime = (double) (endTime - startTime) / itemCount;

        CacheEntry value = cache.getIfPresent(120000);
        System.out.println(value);
        System.out.println("cache size is " + cache.size());
        System.out.println("averageTime to put one item is: " + averageTime);
    }
}
