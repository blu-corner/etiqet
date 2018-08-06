package com.neueda.etiqet.rest.message.impl;

import com.google.api.client.http.*;
import com.google.api.client.util.FieldInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestMsg {

    private String verb;

    private String url;

    private String payload;

    private Map<String, String> headers = new HashMap<>(0);

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    public HttpRequest createHttpRequest(HttpRequestFactory requestFactory, String baseUrl) throws IOException {
        HttpContent requestContent = new EmptyContent();
        if(payload != null) {
            ByteArrayInputStream payloadStream = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
            requestContent = new InputStreamContent("application/json", payloadStream);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        for(Map.Entry<String, String> header : headers.entrySet()) {
            String headerName = header.getKey();
            Object headerValue = header.getValue();

            /*
             * Google's HTTP client doesn't allow you to use set(String, String) on certain headers. The FieldInfo
             * checks this and creates a singleton list on these headers.
             */
            FieldInfo fieldInfo = httpHeaders.getClassInfo().getFieldInfo(headerName);
            if(fieldInfo != null) {
                headerValue = Collections.singletonList(headerValue);
            }
            httpHeaders.set(headerName, headerValue);
        }

        GenericUrl fullUrl = new GenericUrl(baseUrl + "/" + this.url);
        HttpRequest request = requestFactory.buildRequest(verb, fullUrl, requestContent);
        request.setHeaders(httpHeaders);
        request.setThrowExceptionOnExecuteError(false); // allows us to check for errors (e.g. 404, 500)

        return request;
    }

}
