package com.lemma.lemmasignageclient.common.network;

import java.util.Map;

public class Request {
    String url;
    Map<String, String> header;
    Map<String, Object> data;

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", header=" + header +
                ", data=" + data +
                '}';
    }

    public static final class RequestBuilder {
        String url;
        Map<String, String> header;
        Map<String, Object> data;

        private RequestBuilder() {
        }

        public static RequestBuilder aRequest() {
            return new RequestBuilder();
        }

        public RequestBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder withHeader(Map<String, String> header) {
            this.header = header;
            return this;
        }

        public RequestBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public Request build() {
            Request request = new Request();
            request.header = this.header;
            request.data = this.data;
            request.url = this.url;
            return request;
        }
    }
}
