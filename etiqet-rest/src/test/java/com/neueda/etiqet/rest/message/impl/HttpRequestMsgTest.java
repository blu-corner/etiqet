package com.neueda.etiqet.rest.message.impl;

import static org.junit.Assert.assertEquals;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class HttpRequestMsgTest {

    public static HttpRequestFactory getHttpRequestFactory() {
        return new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                        Map<String, List<String>> headers = getHeaders();
                        String responseCode = headers.get("responsecode").get(0);
                        if ("-1".equals(responseCode)) {
                            throw new IOException("ResponseCode -1 sent");
                        }

                        String responseBody = "";
                        if (headers.containsKey("responsebody")) {
                            responseBody = headers.get("responsebody").get(0);
                        }
                        if (headers.containsKey("authorization")) {
                            response.addHeader("authorization", headers.get("authorization").get(0));
                        }
                        response.setContent(responseBody);
                        response.setStatusCode(Integer.parseInt(responseCode));
                        return response;
                    }
                };
            }
        }.createRequestFactory();
    }

    @Test
    public void testVerb() {
        HttpRequestMsg httpMsg = new HttpRequestMsg();
        httpMsg.setVerb("POST");
        assertEquals("POST", httpMsg.getVerb());
        ;
        httpMsg.setVerb("GET");
        assertEquals("GET", httpMsg.getVerb());
        ;
    }

    @Test
    public void testtUrl() {
        HttpRequestMsg httpMsg = new HttpRequestMsg();
        httpMsg.setUrl("http://google.com");
        assertEquals("http://google.com", httpMsg.getUrl());
        ;
    }

    @Test
    public void testPayload() {
        HttpRequestMsg httpMsg = new HttpRequestMsg();
        String payload = "{ \"result\" : \"SUCCESS\" }";
        httpMsg.setPayload(payload);
        assertEquals(payload, httpMsg.getPayload());
    }

    @Test
    public void testHeaders() {
        HttpRequestMsg httpMsg = new HttpRequestMsg();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Cache-Control", "no-cache");
        httpMsg.setHeaders(headers);
        httpMsg.addHeader("Cache-Control", "no-store");
        httpMsg.addHeader("Authorization", "key");

        Map<String, String> actual = httpMsg.getHeaders();
        Map<String, String> expected = new HashMap<>();
        expected.put("Content-Type", "application/json");
        expected.put("Cache-Control", "no-store");
        expected.put("Authorization", "key");

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateHttpRequest() throws IOException {
        String payload = "{ \"result\" : \"SUCCESS\" }";
        String baseUrl = "http://localhost:1000";
        String url = "api/v2";
        String verb = "POST";

        HttpRequestMsg httpMsg = new HttpRequestMsg();
        httpMsg.addHeader("Authorization", "key");
        httpMsg.setPayload(payload);
        httpMsg.setVerb(verb);
        httpMsg.setUrl(url);
        HttpRequest request = httpMsg.createHttpRequest(getHttpRequestFactory(), baseUrl);

        assertEquals(baseUrl + "/" + url, request.getUrl().toString());
        assertEquals("key", request.getHeaders().getAuthorization());
        assertEquals(verb, request.getRequestMethod());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        request.getContent().writeTo(outputStream);
        assertEquals(payload, outputStream.toString(StandardCharsets.UTF_8.toString()));
    }
}
