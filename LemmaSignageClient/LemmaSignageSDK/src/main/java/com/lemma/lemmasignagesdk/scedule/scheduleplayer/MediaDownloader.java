package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import androidx.annotation.NonNull;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

public class MediaDownloader {
    @NonNull
    private final String url;
    @NonNull
    private final String targetPath;
    private MediaDownloaderListener mediaDownloaderListener;

    public MediaDownloader(@NonNull String url, @NonNull String targetPath) {
        this.url = url;
        this.targetPath = targetPath;
    }

    public void setMediaDownloaderListener(MediaDownloaderListener mediaDownloaderListener) {
        this.mediaDownloaderListener = mediaDownloaderListener;
    }

    public void download() {
        FileDownloader.getImpl().create(url)
                .setPath(targetPath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        LMLog.i("DownloadManager", "Download Started " + task.getUrl());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        LMLog.i("DownloadManager", "Download retry " + task.getUrl());
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        mediaDownloaderListener.onSuccess(targetPath);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        mediaDownloaderListener.onError(new Error(e.getLocalizedMessage()));
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LMLog.i("DownloadManager", "Download warn " + task);
                        mediaDownloaderListener.onSuccess(targetPath);
                    }
                }).start();
    }

    public interface MediaDownloaderListener {
        void onSuccess(String targetPath);

        void onError(Error error);
    }

}
