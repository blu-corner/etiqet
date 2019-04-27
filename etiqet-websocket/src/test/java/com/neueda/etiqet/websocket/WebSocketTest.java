package com.neueda.etiqet.websocket;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.junit.runner.RunWith;

@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configClass = WebSocketConfiguration.class,
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/scenarios/websocket_example.feature"
)
public class WebSocketTest {

}
