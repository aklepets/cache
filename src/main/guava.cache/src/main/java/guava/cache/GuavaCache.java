package guava.cache;

import com.google.common.cache.*;

import java.util.concurrent.TimeUnit;

public class GuavaCache {
    public static void main(String[] args) throws InterruptedException {
        Cache<String, Long> cache = CacheBuilder
                .newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();

        RemovalListener<String, String> listener = new RemovalListener<String, String>() {
            int numberItemsEvicted = 0;
            @Override
            public void onRemoval(RemovalNotification<String, String> n){
                if (n.wasEvicted()) {
                    numberItemsEvicted++;
                    String key = n.getKey();
                    String value = n.getValue();
                    String cause = n.getCause().name();
                    System.out.println("key was: " + key + "value was: " + value + "cause was: "+ cause);
                }
            }
        };

        long startTime = System.nanoTime();
        int itemCount = 200_000;
        for (int i = 0; i < itemCount; i++){
            Thread.sleep(1);

            cache.put("item" + i, System.currentTimeMillis());
        }
        long endTime = System.nanoTime();
        double averageTime = endTime - startTime / itemCount;

        Long value = cache.getIfPresent("item199999");
        System.out.println(value);
        System.out.println("cache size is " + cache.size());
        System.out.println("averageTime to put one item is: " + averageTime);
    }
}
