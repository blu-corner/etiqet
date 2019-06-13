package com.neueda.etiqet.messageBroker.client;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.SubscriptionTransport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.ibm.icu.impl.Assert.fail;
import static java.util.Optional.empty;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class MessageBrokerClientTest {
    private MessageBrokerClient messageBrokerClient;
    @Mock private SubscriptionTransport transport;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        messageBrokerClient = new MessageBrokerClient("src/test/resources/config/etiqet.config.xml");
        messageBrokerClient.setTransport(transport);
    }

    @Test
    public void testSendMessageToTopic() throws Exception {
        Cdr cdr = new Cdr("NONE");

        messageBrokerClient.sendMessageToTopic(cdr, empty());

        verify(transport, times(1)).sendToTopic(cdr, empty());
    }

    @Test
    public void testWaitForMsgOnTopic() throws Exception {
        when(transport.consumeFromTopic(Optional.of("TopicName"))).thenReturn(CompletableFuture.completedFuture(new Cdr("NONE")));

        Cdr cdr = messageBrokerClient.waitForMsgOnTopic(Optional.of("TopicName"), 1);

        assertEquals("NONE", cdr.getType());
    }

    @Test
    public void testWaitForMsgOnTopic_timeout() throws Exception {
        when(transport.consumeFromTopic(Optional.of("TopicName"))).thenReturn(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(20);
            } catch (Exception e) {}
            return new Cdr("TYPE");
        }));

        try {
            messageBrokerClient.waitForMsgOnTopic(Optional.of("TopicName"), 1);
            fail("Should have thrown Etiquet exception after timeout");
        } catch (EtiqetException e) {
            assertEquals("java.util.concurrent.TimeoutException", e.getMessage());
        }
    }

}
