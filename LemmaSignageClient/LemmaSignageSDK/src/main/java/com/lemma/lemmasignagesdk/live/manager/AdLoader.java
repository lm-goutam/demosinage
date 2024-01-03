package com.lemma.lemmasignagesdk.live.manager;

import android.net.Uri;
import android.util.Log;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;
import com.lemma.lemmasignagesdk.vast.VastBuilder.VastBuilder;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdLoader {

    private static final String TAG = "AdLoader";
    public NetworkStatusMonitor monitor;
    public DownloadManager downloadManager = null;
    LMDeviceInfo deviceInfo;
    private AdLoaderListener listener;
    private OkHttpClient httpClient;
    private Call httpCall;
    private Boolean executeImpressionInWebContainer = false;
    private int rtbRetryCount = 2;
    private boolean retryWithRTB = false;


    public AdLoader(AdLoaderListener listener) {
        this.listener = listener;
        httpClient = getUnsafeOkHttpClient();
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            OkHttpClient okHttpClient = builder.build();

            return okHttpClient;
        } catch (Exception e) {
            LMLog.w(TAG, "Failed to create custom HTTP client falling back to standard one");
            return new OkHttpClient();
        }
    }

    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer) {
        this.executeImpressionInWebContainer = executeImpressionInWebContainer;
    }

    public boolean isRetryWithRTB() {
        return retryWithRTB;
    }

    public void setRetryWithRTB(boolean retryWithRTB) {
        this.retryWithRTB = retryWithRTB;
    }

    public void destroy() {
        monitor = null;
        downloadManager.destroy();
        downloadManager = null;
        httpClient = null;
        httpCall = null;
        listener = null;
    }

    private void handleSuccess(final Vast vast, final boolean isRTB) {

        // Selects best matching media file, current vast object is updated internally
        LMUtils.filterUnsupportedAds(vast);
        if (vast.isEmpty()) {
            handleError(new Error("After applying media filter, vast became empty"));
        } else {

            // Add sequence if not
            Integer index = 1;
            for (AdI ad : vast.ads) {
                // Generate sequence if not available
                if (ad.sequence == null) {
                    ad.sequence = index;
                    index++;
                }
            }

            if (this.downloadManager != null) {
                this.downloadManager.download(vast, new DownloadManager.CompletionCallback() {
                    @Override
                    public void onDownloadComplete(ArrayList<AdI> ads) {
                        Vast newVast = new VastBuilder().copyWithNewAdList(vast, ads);
                        if (AdLoader.this.listener != null) {
                            newVast.isRTB = isRTB;
                            AdLoader.this.listener.onSuccess(newVast);
                        }
                    }
                });
            } else {
                LMLog.w(TAG, "DownloadManager is not provided in ad loader, so all ads will have remote URLs");
                if (AdLoader.this.listener != null) {
                    vast.isRTB = isRTB;
                    this.listener.onSuccess(vast);
                }
            }
        }
    }

    private void handleError(Error error) {

        if (AdLoader.this.listener != null) {
            this.listener.onError(error);
        }
    }

    public void load(final String url) {
        load(url, false);
    }

    public void load(final String url, final boolean isRTB) {
        if (monitor != null && !monitor.isNetworkConnected()) {
            handleError(new Error("Network is not available"));
            return;
        }

        Request request = null;
        if (executeImpressionInWebContainer && deviceInfo != null) {
            String userAgent = deviceInfo.getUserAgent();
            request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", userAgent)
                    .build();
            Log.i(TAG, "Using custom User-Agent " + userAgent);
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        LMLog.i(TAG, "Requesting URL : " + url + " with RTB ? " + isRTB);
        httpCall = httpClient.newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LMLog.e(TAG, e.getLocalizedMessage());
                handleError(new Error(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String vastXML = response.body().string();
                LMLog.i(TAG, "Response : " + vastXML);

                try {
                    if (vastXML != null && vastXML.length() <= 0) {
                        if (isRetryWithRTB()) {
                            if (rtbRetryCount == 2) {
                                --rtbRetryCount;
                                LMLog.i(TAG, "Retrying");
                                load(url);
                            } else if (rtbRetryCount == 1) {
                                --rtbRetryCount;
                                LMLog.i(TAG, "Retrying with RTB");
                                retryRTB(url);
                            } else {
                                handleError(new Error("Empty Vast"));
                            }
                        } else {
                            handleError(new Error("Empty Vast"));
                        }
                    } else {
                        Vast vast = new VastBuilder().buildWithJson(vastXML);
                        if (vast == null) {
                            vast = new VastBuilder().build(vastXML);
                        }
                        if (vast.ads != null && vast.ads.size() > 0) {
                            handleSuccess(vast, isRTB);
                        } else {
                            handleError(new Error("Empty Vast"));
                        }
                    }
                } catch (Exception e) {
                    handleError(new Error(e));
                }

            }
        });
    }

    private void retryRTB(String url) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("rtb", "1");

        String rtbUrl = builder.build().toString();
        load(rtbUrl, true);
    }

    public interface AdLoaderListener {
        void onSuccess(Vast vast);

        void onError(Error err);
    }
}
