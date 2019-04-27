package com.neueda.etiqet.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configClass = RestConfig.class,
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/scenarios/ok/rest_example.feature"
)
public class RestTest {

    private static final int WIRE_MOCK_PORT = 9999;
    private static WireMockServer wireMockServer;

    @BeforeClass
    public static void setUp() {
        wireMockServer = new WireMockServer(WIRE_MOCK_PORT);
        wireMockServer.start();

        configureFor("localhost", WIRE_MOCK_PORT);
        stubFor(get(urlEqualTo("/test"))
            .willReturn(aResponse()
                .withBody("{\"response\": \"ok\", \"code\": 200}")
                .withStatus(200)
                .withHeader("Content-Type", "application/json")));
        stubFor(post(urlEqualTo("/test"))
            .willReturn(aResponse()
                .withBody("{\"response\": \"ok\", \"code\": 200}")
                .withStatus(200)
                .withHeader("Content-Type", "application/json")));
        stubFor(post(urlEqualTo("/authRequired"))
            .withHeader("Authorization", equalTo("LEGITIMATE_AUTH_PASSWORD"))
            .willReturn(aResponse()
                .withBody("{\"response\": { \"message\": \"protected_information\", \"login\": true }, \"code\": 200}")
                .withStatus(200)
                .withHeader("Content-Type", "application/json")));
        stubFor(post(urlEqualTo("/dataNeeded"))
            .withRequestBody(equalToJson("{\"test\": \"value\"}"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"response\": { \"message\": \"received_ok\" }, \"code\": 200}")));
    }

    @AfterClass
    public static void tearDown() {
        wireMockServer.shutdown();
    }

}
