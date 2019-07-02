package com.neueda.etiqet.messageBroker.client;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.BrokerTransport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static com.ibm.icu.impl.Assert.fail;
import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static java.util.Optional.empty;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class MessageBrokerClientTest {
    private MessageBrokerClient messageBrokerClient;
    @Mock private BrokerTransport transport;

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
        when(transport.subscribeAndConsumeFromTopic(Optional.of("TopicName"), Duration.ofMillis(1))).thenReturn(new Cdr("NONE"));

        Cdr cdr = messageBrokerClient.waitForMsgOnTopic(Optional.of("TopicName"), 1);

        assertEquals("NONE", cdr.getType());
    }

    @Test
    public void testSubscribeToTopic() throws Exception {
        final String topicName = "topic";
        doAnswer(
            call -> {
                Consumer listener = call.getArgument(1);
                listener.accept(aCdr("NONE").build());
                listener.accept(aCdr("NONE").build());
                return null;
            }
        ).when(transport).subscribeToTopic(eq(Optional.of(topicName)), any(Consumer.class));

        messageBrokerClient.subscribeToTopic(topicName);

        assertEquals(2, messageBrokerClient.getReceivedMessagesFromTopic(topicName).size());
    }

    @Test
    public void getReceivedMessagesFromTopic_failOnNullMessage() throws Exception {
        final String topicName = "topic";
        doAnswer(
            call -> {
                Consumer listener = call.getArgument(1);
                listener.accept(aCdr("NONE").build());
                listener.accept(null);
                return null;
            }
        ).when(transport).subscribeToTopic(eq(Optional.of(topicName)), any(Consumer.class));

        try {
            messageBrokerClient.subscribeToTopic(topicName);
            fail("Should have failed after after receiving null message");
        } catch (EtiqetRuntimeException e){}

    }

}
