package com.neueda.etiqet.transport.amqp.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.transport.delegate.ByteArrayConverterDelegate;
import com.neueda.etiqet.core.transport.delegate.StringBinaryMessageConverterDelegate;
import com.neueda.etiqet.transport.amqp.AmqpConfiguration;
import com.neueda.etiqet.transport.amqp.Exchanges;
import com.neueda.etiqet.transport.amqp.Queues;
import com.neueda.etiqet.transport.amqp.config.model.AmqpConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AmqpConfigExtractorTest {
    @Mock
    private AmqpConfigXmlParser amqpConfigXmlParser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void retrieveConfiguration_shouldSetByteArrayAsDefaultBinaryMessageConverterClass() throws Exception {
        assertEquals(ByteArrayConverterDelegate.class,
            retrieveConfig(null).getBinaryMessageConverterDelegateClass());
    }

    @Test
    public void retrieveConfiguration_shouldSetStringBinaryMessageConverterClass() throws Exception {
        assertEquals(StringBinaryMessageConverterDelegate.class,
            retrieveConfig(StringBinaryMessageConverterDelegate.class.getName()).getBinaryMessageConverterDelegateClass());
    }

    private AmqpConfig retrieveConfig(final String binaryMessageConverterDelegateClass) throws EtiqetException {
        AmqpConfiguration amqpConfiguration = new AmqpConfiguration();
        amqpConfiguration.setQueues(new Queues());
        amqpConfiguration.setExchanges(new Exchanges());
        amqpConfiguration.setBinaryMessageConverterDelegate(binaryMessageConverterDelegateClass);
        when(amqpConfigXmlParser.parse(anyString())).thenReturn(amqpConfiguration);

        AmqpConfigExtractor amqpConfigExtractor = new AmqpConfigExtractor(amqpConfigXmlParser);
        return amqpConfigExtractor.retrieveConfiguration("");
    }
}
