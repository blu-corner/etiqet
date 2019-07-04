package com.neueda.etiqet.jms;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Rule;
import org.junit.runner.RunWith;


/**
 * Disabled temporarily since two EtiqetTestRunners cannot run properly in the same module. Fix still needed
 */

//@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configFile = "src/test/resources/config/protobuf/etiqet.protobuf.config.xml",
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features/jms_protobuf_test.feature"
)
public class JmsProtobufTest {

    @Rule
    public EmbeddedActiveMQBroker broker;

}



