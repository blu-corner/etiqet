package com.neueda.etiqet.solace;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.junit.runner.RunWith;

/**
 * This class class the basic solace_test.feature in the test classpath. This feature requires a running Solace instance
 * so the feature has been commented out to prevent build failures. The feature remains as an indication of Etiqet
 * fixtures that can be used to interact with a Solace bus.
 */
@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    configFile = "src/test/resources/config/etiqet.config.xml",
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features/solace_test.feature"
)
public class SolaceTest {
}
