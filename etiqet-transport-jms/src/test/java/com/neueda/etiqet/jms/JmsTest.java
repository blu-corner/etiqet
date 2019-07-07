package com.neueda.etiqet.jms;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.*;
import org.junit.runner.RunWith;



@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configFile = "src/test/resources/config/etiqet.config.xml",
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features/jms_test.feature"
)
public class JmsTest {

    @Rule
    public static EmbeddedActiveMQBroker broker;
}


