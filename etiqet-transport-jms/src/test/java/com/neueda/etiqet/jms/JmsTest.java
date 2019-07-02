package com.neueda.etiqet.jms;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;



/**
 * This class class the basic jms_test.feature in the test classpath. This feature requires a running Jms instance
 * so the feature has been commented out to prevent build failures. The feature remains as an indication of Etiqet
 * fixtures that can be used to interact with a Jms bus.
 */
@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configFile = "src/test/resources/config/etiqet.config.xml",
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features/jms_test.feature"
)
public class JmsTest {
    private static EmbeddedActiveMQBroker broker;

    @BeforeClass
    public static void setupClass() {
        broker = new EmbeddedActiveMQBroker();
        broker.start();
    }

    @AfterClass
    public static void shutdownClass() {
        broker.stop();
     }

}


