package com.neueda.etiqet.rabbitmq;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import com.neueda.etiqet.rabbitmq.embeddedBroker.EmbeddedQpidBrokerRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
        configFile = "src/test/resources/config/etiqet.config.xml",
        plugin = {"pretty", "html:target/cucumber"},
        features = "src/test/resources/scenarios/rabbitmq.feature")
public class RabbitMqTest {

    @ClassRule
    public static EmbeddedQpidBrokerRule qpidBrokerRule = new EmbeddedQpidBrokerRule();

}
