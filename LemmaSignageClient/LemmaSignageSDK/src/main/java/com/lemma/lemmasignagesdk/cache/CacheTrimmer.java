package com.lemma.lemmasignagesdk.cache;

import android.net.Uri;

import com.lemma.lemmasignagesdk.common.LMLog;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class CacheTrimmer {
    private final File file;
    private final ExecutorService workerThread = Executors.newSingleThreadExecutor();
    private double limitInMb;
    private int filesCountToCleanOnLimit = 5;

    public CacheTrimmer(File file, double limit) {
        this.file = file;
        this.limitInMb = limit;
    }

    public void setLimitInMb(double limitInMb) {
        this.limitInMb = limitInMb;
    }

    public void setFilesCountToCleanOnLimit(int filesCountToCleanOnLimit) {
        this.filesCountToCleanOnLimit = filesCountToCleanOnLimit;
    }

    public long getFolderSize(File dir) {
        long size = 0;
        try {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else
                    size += getFolderSize(file);
            }
        } catch (NullPointerException exception) {
            LMLog.e("Failed to get files in cache, may fail to clean the cache");
        }
        return size;
    }

    public void adjustCacheInBackground() {
        Box<CacheEntry> cacheEntryBox = ObjectBox.get().boxFor(CacheEntry.class);
        TouchCallable touchCallable = new TouchCallable(cacheEntryBox, file);
        touchCallable.setFilesCountToCleanOnLimit(filesCountToCleanOnLimit);
        workerThread.submit(touchCallable);
    }

    private class TouchCallable implements Callable<Void> {

        private final Box<CacheEntry> cacheEntryBox;
        private final File file;
        private int filesCountToCleanOnLimit = 5;

        public TouchCallable(Box<CacheEntry> cacheEntryBox, File file) {
            this.cacheEntryBox = cacheEntryBox;
            this.file = file;
        }

        public void setFilesCountToCleanOnLimit(int filesCountToCleanOnLimit) {
            this.filesCountToCleanOnLimit = filesCountToCleanOnLimit;
        }

        @Override
        public Void call() {
            touchInBackground(this.cacheEntryBox);
            return null;
        }

        private void touchInBackground(Box<CacheEntry> cacheEntryBox) {
            double size = getFolderSize(this.file) / (1024.0 * 1024.0);

            if (size >= limitInMb) {
                Query<CacheEntry> query = cacheEntryBox.query().order(CacheEntry_.lastAccessed).build();
                List<CacheEntry> cacheEntries = query.find(0, this.filesCountToCleanOnLimit);
                trim(cacheEntryBox, cacheEntries);
                LMLog.i("Planing to delete");
            } else {
                LMLog.i("Cache still in shape");
            }
        }

        private void trim(Box<CacheEntry> cacheEntryBox, List<CacheEntry> cacheEntries) {
            for (CacheEntry cacheEntry : cacheEntries) {
                Uri uri = Uri.parse(cacheEntry.localUriString);
                File file = new File(uri.getPath());
                if (file.exists()) {
                    if (file.delete()) {
                        cacheEntryBox.remove(cacheEntry);
                        LMLog.i("Deleted file - " + uri.getPath());
                    } else {
                        LMLog.i("File does not exist to be deleted");
                    }
                }
            }
        }
    }
}
