package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.webkit.URLUtil;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.common.LSSConfig;

public class MediaRepository {

    public MediaRepository() {

    }

    public void get(String url, Callback callback) {

        String key = LMUtils.getCrc32(url);
        String localPath = LSSConfig.fileCache.get(key);
        if (localPath != null) {
            LMLog.i("Using cached file at path - " + localPath);
            callback.onCompletion(null, localPath);
            return;
        }

        String fileName = URLUtil.guessFileName(url, null, null);
        String target = LMUtils.getFilePathInRootDir(fileName, key);
        MediaDownloader mediaDownloader = new MediaDownloader(url, target);
        mediaDownloader.setMediaDownloaderListener(
                new MediaDownloader.MediaDownloaderListener() {
                    @Override
                    public void onSuccess(String targetPath) {
                        String uriString = "file://" + targetPath;

                        String key = LMUtils.getCrc32(url);
                        LSSConfig.fileCache.put(key, uriString, url);
                        callback.onCompletion(null, uriString);
                        LMLog.i("Downloaded file at path - " + uriString);
                    }

                    @Override
                    public void onError(Error error) {
                        callback.onCompletion(error, null);
                    }
                }
        );
        mediaDownloader.download();
    }

    public interface Callback {
        void onCompletion(Error error, String uriString);
    }
}
