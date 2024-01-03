package com.lemma.lemmasignageclient.common.network;

import android.net.Uri;

import com.lemma.lemmasignageclient.common.logger.Applogger;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class NetworkHandler {

    private final OkHttpClient httpClient;
    private CompletionCallback listener;

    public NetworkHandler() {
        this.httpClient = new OkHttpClient();
    }

    public void post(Request aRequest,
                     final CompletionCallback completionCallback) {

        JSONObject paramJson = new JSONObject(aRequest.getData());
        okhttp3.Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        request = new okhttp3.Request.Builder()
                .url(aRequest.getUrl())
                .post(body)
                .build();
        processRequest(request, completionCallback);
    }

    public void get(String url,
                    final CompletionCallback completionCallback) {

        Request request = Request.RequestBuilder.aRequest()
                .withUrl(url)
                .build();
        get(request, completionCallback);
    }

    public void get(Request aRequest,
                    final CompletionCallback completionCallback) {

        Uri.Builder queryBuilder = Uri.parse(aRequest.getUrl()).buildUpon();
        if (aRequest.getData() != null) {
            for (Map.Entry<String, Object> entry : aRequest.getData().entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());

                if (key != null && value != null) {
                    queryBuilder.appendQueryParameter(key, value);
                }
            }
        }
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
                .url(queryBuilder.build().toString())
                .get();

        if (aRequest.getHeader() != null) {
            for (Map.Entry<String, String> entry : aRequest.getHeader().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key != null && value != null) {
                    builder.addHeader(key, value);
                }
            }
        }
        okhttp3.Request request = builder.build();

        processRequest(request, completionCallback);
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "NA";
        }
    }

    private void logRequest(okhttp3.Request request) {
        String reqInfo = "Request{" +
                "url='" + request.url() + '\'' +
                ", header=" + request.headers().toString() +
                ", data=" + bodyToString(request.body()) +
                '}';
        Applogger.i("Request - " + reqInfo);
    }

    private void processRequest(okhttp3.Request request,
                                final CompletionCallback completionCallback) {
//        logRequest(request);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Error error = new Error(e.getLocalizedMessage());
                completionCallback.onComplete(error, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String result = response.body().string();
                    if (result != null && result.length() > 0) {
//                        LMLog.i("Response - "+result);
                        completionCallback.onComplete(null, result);
                    } else {
                        String res = response.body().string();
                        if (res == null) {
                            res = "Invalid response";
                        }
                        Error error = new Error(res);
                        Applogger.i("Response error - " + res);
                        completionCallback.onComplete(error, null);
                    }

                } catch (Exception e) {
                    Error error = new Error(e.getMessage());
                    completionCallback.onComplete(error, null);
                }
            }
        });
    }

    public interface CompletionCallback {
        void onComplete(Error error, String response);
    }

}
