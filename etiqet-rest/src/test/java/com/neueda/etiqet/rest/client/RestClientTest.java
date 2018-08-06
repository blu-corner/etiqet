package com.neueda.etiqet.rest.client;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RestClientTest {

    private RestClient client;

    @Before
    public void setUp() throws EtiqetException {
        // override the HttpRequestFactory and Config objects for testing purposes
        String primaryConfig = "${etiqet.directory}/etiqet-rest/src/test/resources/config/ok/client.cfg";
        String secondaryConfig = "${etiqet.directory}/etiqet-rest/src/test/resources/config/ok/secondary_client.cfg";
        client = new RestClient(primaryConfig, secondaryConfig) {
            @Override
            HttpRequestFactory getHttpRequestFactory() {
                return new MockHttpTransport() {
                    @Override
                    public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
                        return new MockLowLevelHttpRequest() {
                            @Override
                            public LowLevelHttpResponse execute() throws IOException {
                                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                                Map<String, List<String>> headers = getHeaders();
                                String responseCode = headers.get("responsecode").get(0);
                                if("-1".equals(responseCode)) {
                                    throw new IOException("ResponseCode -1 sent");
                                }

                                String responseBody = headers.get("responsebody").get(0);
                                response.setContent(responseBody);
                                response.setStatusCode(Integer.parseInt(responseCode));
                                return response;
                            }
                        };
                    }
                }.createRequestFactory();
            }
        };
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testIsAdmin() {
        for(String msgType : Arrays.asList("", "200", "404", "GET", "PUT", "POST", "DELETE", "301"))
            assertFalse(client.isAdmin(msgType));
    }

    @Test
    public void testDefaultSession() {
        assertEquals("", client.getDefaultSessionId());
    }

    @Test
    public void testIsLoggedOn() {
        assertFalse(client.isLoggedOn());
    }

    @Test
    public void testLaunchClient() throws EtiqetException {
        assertNull(client.getRequestFactory());
        client.launchClient();
        assertNotNull(client.getRequestFactory());
    }

    @Test
    public void testFailover() throws EtiqetException {
        assertTrue("Client was instantiated with 2 config files, so should be able to failover",
                        client.canFailover());
        client.launchClient();
        assertEquals("https://api-fxpractice.oanda.com/v3/", client.getClientConfig().getString("baseUrl"));
        client.failover();
        assertEquals("https://api-fxpractice2.oanda.com/v3/", client.getClientConfig().getString("baseUrl"));
        client.failover();
        assertEquals("https://api-fxpractice.oanda.com/v3/", client.getClientConfig().getString("baseUrl"));
    }

    @Test
    public void testSendMessage() throws EtiqetException {
        Cdr data = new Cdr("GET");
        data.set("$httpEndpoint", "/test/api");
        data.set("$httpVerb", "GET");
        data.set("$header.responsecode", "200");
        data.set("$header.responsebody", "{ text : \"Everything OK\" }");

        client.launchClient();
        client.send(data);
        Cdr cdr = client.waitForMsgType("200", 5000);
        assertTrue(cdr.containsKey("text"));
        assertEquals("Everything OK", cdr.getAsString("text"));

        data.set("$httpEndpoint", "/test/api");
        data.set("$httpVerb", "GET");
        data.set("$header.responsecode", "-1");
        try {
            client.send(data);
            fail("Should have thrown an exception because the response code was set to -1");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Error sending HTTP Request", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("ResponseCode -1 sent", e.getCause().getMessage());
        }
    }

}