package com.neueda.etiqet.core.testing;

import com.neueda.etiqet.core.EtiqetOptions;

@EtiqetOptions(
    configFile = "${etiqet.directory}/etiqet-core/src/test/resources/config/etiqet.config.xml",
    features = {"${etiqet.directory}/etiqet-core/src/test/resources/features/test.feature"},
    additionalFixtures = "com.example.other.fixtures",
    plugin = {"pretty", "html:target/cucumber"}
)
public class ValidConfigurationFileTestRun {

}
