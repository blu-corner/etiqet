package com.neueda.etiqet.core.testing;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.config.annotations.impl.ExampleConfiguration;

@EtiqetOptions(
    configClass = ExampleConfiguration.class,
    configFile = "${etiqet.directory}/etiqet-core/src/test/resources/config/etiqet.config.xml",
    features = {"src/test/resources/features/test.feature"},
    additionalFixtures = "com.example.other.fixtures",
    plugin = {"pretty", "html:target/cucumber"}
)
public class DoubleConfigurationTestRun {

}
