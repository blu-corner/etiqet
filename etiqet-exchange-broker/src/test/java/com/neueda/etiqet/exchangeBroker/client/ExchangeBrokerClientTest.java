package com.neueda.etiqet.exchangeBroker.client;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.ExchangeTransport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

public class ExchangeBrokerClientTest {
    private ExchangeBrokerClient client;
    @Mock private ExchangeTransport transport;

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
        client = new ExchangeBrokerClient("src/test/resources/config/etiqet.config.xml");
        client.setTransport(transport);
    }

    @Test
    public void testSubscribeToQueue() throws Exception {
        final String queueName = "queue";
        doAnswer(
            call -> {
                Consumer<Cdr> listener = call.getArgument(1);
                listener.accept(aCdr("NONE").build());
                return null;
            }
        ).when(transport).subscribeToQueue(eq(queueName), any(Consumer.class));

        client.subscribeToQueue(queueName);

        List<Cdr> receivedMessages = client.getReceivedMessagesFromQueue(queueName);
        assertEquals(1, receivedMessages.size());
        assertEquals("NONE", receivedMessages.get(0).getType());
    }

    @Test
    public void testSubscribeToQueue_noMessages() throws Exception {
        final String queueName = "queue";
        doAnswer(
            call -> null
        ).when(transport).subscribeToQueue(eq(queueName), any(Consumer.class));

        client.subscribeToQueue(queueName);

        List<Cdr> receivedMessages = client.getReceivedMessagesFromQueue(queueName);
        assertTrue(receivedMessages.isEmpty());
    }

    @Test
    public void testSubscriteToQueue_twoMessagesOnSameQueue() throws Exception {
        final String queueName1 = "queue1", queueName2 = "queue2";
        doAnswer(
            call -> {
                Consumer<Cdr> listener = call.getArgument(1);
                listener.accept(aCdr("NONE").build());
                listener.accept(aCdr("NONE").build());
                return null;
            }
        ).when(transport).subscribeToQueue(eq(queueName1), any(Consumer.class));
        doAnswer(
            call -> null
        ).when(transport).subscribeToQueue(eq(queueName2), any(Consumer.class));

        client.subscribeToQueue(queueName1);
        client.subscribeToQueue(queueName2);

        List<Cdr> receivedMessages1 = client.getReceivedMessagesFromQueue(queueName1);
        assertEquals(2, receivedMessages1.size());
        assertEquals("NONE", receivedMessages1.get(0).getType());
        assertTrue(client.getReceivedMessagesFromQueue(queueName2).isEmpty());
        assertTrue(client.getReceivedMessagesFromQueue("invalidQueueName").isEmpty());
    }

    @Test
    public void testGetLastMessageFromQueue() throws Exception {
        final String queueName = "queue";
        doAnswer(
            call -> {
                Consumer<Cdr> listener = call.getArgument(1);
                listener.accept(aCdr("M1").build());
                listener.accept(aCdr("M2").build());
                return null;
            }
        ).when(transport).subscribeToQueue(eq(queueName), any(Consumer.class));

        client.subscribeToQueue(queueName);
        Optional<Cdr> lastMessage = client.getLastMessageFromQueue(queueName);

        assertTrue(lastMessage.isPresent());
        assertEquals("M2", lastMessage.get().getType());
    }

    @Test
    public void testGetLastMessageFromQueue_noMessage() throws Exception {

        Optional<Cdr> lastMessage = client.getLastMessageFromQueue("queue");

        assertFalse(lastMessage.isPresent());
    }
}
