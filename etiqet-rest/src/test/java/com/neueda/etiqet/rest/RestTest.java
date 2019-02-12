package com.neueda.etiqet.rest;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.junit.runner.RunWith;

@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/scenarios/ok/rest_example.feature"
)
public class RestTest { }
