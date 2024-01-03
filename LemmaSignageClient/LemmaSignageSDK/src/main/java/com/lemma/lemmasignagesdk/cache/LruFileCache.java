package com.lemma.lemmasignagesdk.cache;

import java.io.File;
import java.util.Date;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class LruFileCache {

    private final Box<CacheEntry> cacheEntryBox;
    private final CacheTrimmer cacheTrimmer;

    public LruFileCache(File folder, double limitInMb) {
        cacheEntryBox = ObjectBox.get().boxFor(CacheEntry.class);
        cacheTrimmer = new CacheTrimmer(folder, limitInMb);
    }

    public void put(String key, String localPath, String url) {

        CacheEntry cacheEntry = new CacheEntry();
        cacheEntry.localUriString = localPath;
        cacheEntry.urlString = url;
        cacheEntry.createdAt = new Date();
        cacheEntry.lastAccessed = new Date();
        cacheEntry.urlCRC32Hash = key;

        cacheEntryBox.put(cacheEntry);

        cacheTrimmer.adjustCacheInBackground();
        // On bg thread
        //1. Update file size
        //2. Check free space
        //3. Check the total cache & trim if needed
    }

    public String get(String key) {

        Query<CacheEntry> query = cacheEntryBox.query(CacheEntry_.urlCRC32Hash.equal(key)).build();
        CacheEntry cacheEntry = query.findFirst();
        if (cacheEntry == null) {
            return null;
        }
        cacheEntry.lastAccessed = new Date();
        cacheEntryBox.put(cacheEntry);
        query.close();
        return cacheEntry.localUriString;
    }
}
