package com.neueda.etiqet.messageBroker;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
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
    features = "src/test/resources/features/broker_test.feature"
)
public class MessageBrokerTest {


}


