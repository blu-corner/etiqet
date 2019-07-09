package com.neueda.etiqet.transport.jms.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.transport.jms.ArgType;
import com.neueda.etiqet.transport.jms.JmsConfiguration;
import com.neueda.etiqet.transport.jms.config.model.ConstructorArgument;
import com.neueda.etiqet.transport.jms.config.model.JmsConfig;
import com.neueda.etiqet.transport.jms.config.model.SetterArgument;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.neueda.etiqet.transport.jms.config.model.ArgumentType.BOOLEAN;
import static com.neueda.etiqet.transport.jms.config.model.ArgumentType.STRING;
import static com.neueda.etiqet.transport.jms.config.model.ConstructorArgBuilder.aConstructorArg;
import static com.neueda.etiqet.transport.jms.config.model.JmsConfigurationBuilder.aJmsConfigurationBuilder;
import static com.neueda.etiqet.transport.jms.config.model.SetterPropertyBuilder.aSetterProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class JmsConfigExtractorTest {
    @Mock
    private JmsConfigXmlParser jmsConfigXmlParser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBasicConfig() throws Exception {
        JmsConfiguration jmsConfiguration = aJmsConfigurationBuilder()
            .withImplementation("org.apache.activemq.ActiveMQConnectionFactory")
            .addConstructorArg(aConstructorArg().argType(ArgType.STRING).argValue("USERNAME").build())
            .addConstructorArg(aConstructorArg().argType(ArgType.STRING).argValue("PASSWORD").build())
            .build();
        when(jmsConfigXmlParser.parse(anyString(), eq(JmsConfiguration.class))).thenReturn(jmsConfiguration);
        JmsConfigExtractor jmsConfigExtractor = new JmsConfigExtractor(jmsConfigXmlParser);

        JmsConfig jmsConfig = jmsConfigExtractor.retrieveConfiguration("file");

        assertEquals(ActiveMQConnectionFactory.class, jmsConfig.getImplementation());
        assertEquals(2, jmsConfig.getConstructorArgs().size());
        ConstructorArgument arg1 = jmsConfig.getConstructorArgs().get(0);
        assertEquals(STRING, arg1.getArgumentType());
        assertEquals("USERNAME", arg1.getValue());
        ConstructorArgument arg2 = jmsConfig.getConstructorArgs().get(1);
        assertEquals(STRING, arg2.getArgumentType());
        assertEquals("PASSWORD", arg2.getValue());
    }

    @Test
    public void testSetters() throws Exception {
        JmsConfiguration jmsConfiguration = aJmsConfigurationBuilder()
            .withImplementation("org.apache.activemq.ActiveMQConnectionFactory")
            .addProperty(aSetterProperty().argType(ArgType.STRING).name("username").argValue("USERNAME").build())
            .addProperty(aSetterProperty().argType(ArgType.STRING).name("password").argValue("PASSWORD").build())
            .build();
        when(jmsConfigXmlParser.parse(anyString(), eq(JmsConfiguration.class))).thenReturn(jmsConfiguration);
        JmsConfigExtractor jmsConfigExtractor = new JmsConfigExtractor(jmsConfigXmlParser);

        JmsConfig jmsConfig = jmsConfigExtractor.retrieveConfiguration("file");

        assertEquals(ActiveMQConnectionFactory.class, jmsConfig.getImplementation());
        assertEquals(2, jmsConfig.getSetterArgs().size());
        SetterArgument arg1 = jmsConfig.getSetterArgs().get(0);
        assertEquals(STRING, arg1.getArgumentType());
        assertEquals("USERNAME", arg1.getValue());
        SetterArgument arg2 = jmsConfig.getSetterArgs().get(1);
        assertEquals(STRING, arg2.getArgumentType());
        assertEquals("PASSWORD", arg2.getValue());
    }

    @Test(expected = EtiqetException.class)
    public void invalidImplementation() throws Exception {
        JmsConfiguration jmsConfiguration = aJmsConfigurationBuilder()
            .withImplementation("invalid.class")
            .build();
        when(jmsConfigXmlParser.parse(anyString(), eq(JmsConfiguration.class))).thenReturn(jmsConfiguration);
        JmsConfigExtractor jmsConfigExtractor = new JmsConfigExtractor(jmsConfigXmlParser);

        jmsConfigExtractor.retrieveConfiguration("file");
    }

    @Test
    public void testBooleanArguments() throws Exception {
        JmsConfiguration jmsConfiguration = aJmsConfigurationBuilder()
            .withImplementation("org.apache.activemq.ActiveMQConnectionFactory")
            .addConstructorArg(aConstructorArg().argType(ArgType.BOOLEAN).argValue("true").build())
            .addProperty(aSetterProperty().argType(ArgType.BOOLEAN).argValue("FALSE").build())
            .build();
        when(jmsConfigXmlParser.parse(anyString(), eq(JmsConfiguration.class))).thenReturn(jmsConfiguration);
        JmsConfigExtractor jmsConfigExtractor = new JmsConfigExtractor(jmsConfigXmlParser);

        JmsConfig jmsConfig = jmsConfigExtractor.retrieveConfiguration("file");

        assertEquals(ActiveMQConnectionFactory.class, jmsConfig.getImplementation());
        assertEquals(1, jmsConfig.getConstructorArgs().size());
        ConstructorArgument arg1 = jmsConfig.getConstructorArgs().get(0);
        assertEquals(BOOLEAN, arg1.getArgumentType());
        assertEquals(Boolean.TRUE, arg1.getValue());
        SetterArgument arg2 = jmsConfig.getSetterArgs().get(0);
        assertEquals(BOOLEAN, arg2.getArgumentType());
        assertEquals(Boolean.FALSE, arg2.getValue());
    }
}
