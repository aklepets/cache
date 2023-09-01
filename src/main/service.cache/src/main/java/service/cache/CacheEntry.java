package service.cache;

import java.util.Objects;

public class CacheEntry {
    private String data;
    public CacheEntry(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
