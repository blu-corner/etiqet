package com.neueda.etiqet.core;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.annotations.impl.ExampleConfiguration;
import com.neueda.etiqet.core.testing.DoubleConfigurationTestRun;
import com.neueda.etiqet.core.testing.EmptyOptionsTestRun;
import com.neueda.etiqet.core.testing.NoOptionsTestRun;
import com.neueda.etiqet.core.testing.ValidConfigurationClassTestRun;
import com.neueda.etiqet.core.testing.ValidConfigurationFileTestRun;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.junit.FeatureRunner;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class EtiqetTestRunnerTest {

    private String defaultConfigVar;

    @Before
    public void setUp() {
        defaultConfigVar = System.getProperty(DEFAULT_CONFIG_VARIABLE);
    }

    @After
    public void tearDown() {
        if (defaultConfigVar != null) {
            System.setProperty(DEFAULT_CONFIG_VARIABLE, defaultConfigVar);
        }
    }

    private void resetGlobalConfig() throws Exception {
        // because GlobalConfig is a singleton, we need to ensure that the instance is reset after each of these
        // tests. Without exposing the instance, this involves using reflection to set the instance to null
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        Field field = GlobalConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(globalConfig, null);
    }

    @Test
    public void testExceptionWithNoEtiqetOptions() {
        try {
            new EtiqetTestRunner(NoOptionsTestRun.class);
            fail("java.lang.String does not have the @EtiqetOptions annotation, so should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("EtiqetTestRunner requires the @EtiqetOptions annotation", e.getMessage());
        }
    }

    @Test
    public void testExceptionWhenNoConfigSpecified() {
        try {
            System.clearProperty(DEFAULT_CONFIG_VARIABLE);
            new EtiqetTestRunner(EmptyOptionsTestRun.class);
            fail("EmptyOptionsTestRun should not specify any options, so this should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("EtiqetTestRunner requires one of the following configuration "
                + "options to be specified:"
                + System.lineSeparator()
                + "\t1. Specifying a configuration class via the configClass option"
                + System.lineSeparator()
                + "\t2. Specifying a configuration file using the configFile option"
                + System.lineSeparator()
                + "\t3. Setting the Environment / System variable " + DEFAULT_CONFIG_VARIABLE + " with the path "
                + "to a configuration file", e.getMessage());
        }
    }

    @Test
    public void testEtiqetOptionsParsedCorrectly() throws Exception {
        resetGlobalConfig();

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationClassTestRun.class) {
            @Override
            Runtime getCucumberRuntime(ResourceLoader resourceLoader,
                ClassFinder classFinder,
                ClassLoader classLoader,
                RuntimeOptions runtimeOptions) {
                assertTrue(runtimeOptions.getGlue().contains(EtiqetTestRunner.FIXTURE_PACKAGE));
                assertTrue(runtimeOptions.getGlue().contains("com.example.other.fixtures"));
                try {
                    assertTrue(runtimeOptions.getFeaturePaths().contains(Environment
                        .resolveEnvVars("${etiqet.directory}/etiqet-core/src/test/resources/features/test.feature")));
                } catch (EtiqetException e) {
                    fail("Unable to resolve ${etiqet.directory} which should exist");
                }
                return mock(Runtime.class);
            }
        };

        GlobalConfig globalConfig = testRunner.getGlobalConfig();
        assertNotNull(globalConfig);
        assertNotNull(globalConfig.getConfigClass());
        assertEquals(ExampleConfiguration.class, globalConfig.getConfigClass());

        resetGlobalConfig();
    }

    @Test
    public void testEtiqetOptionsParsedCorrectlyWithConfigurationFile() throws Exception {
        resetGlobalConfig();

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationFileTestRun.class) {
            @Override
            Runtime getCucumberRuntime(ResourceLoader resourceLoader,
                ClassFinder classFinder,
                ClassLoader classLoader,
                RuntimeOptions runtimeOptions) {
                assertTrue(runtimeOptions.getGlue().contains(EtiqetTestRunner.FIXTURE_PACKAGE));
                assertTrue(runtimeOptions.getGlue().contains("com.example.other.fixtures"));
                try {
                    assertTrue(runtimeOptions.getFeaturePaths().contains(Environment
                        .resolveEnvVars("${etiqet.directory}/etiqet-core/src/test/resources/features/test.feature")));
                } catch (EtiqetException e) {
                    fail("Unable to resolve ${etiqet.directory} which should exist");
                }
                return mock(Runtime.class);
            }
        };

        GlobalConfig globalConfig = testRunner.getGlobalConfig();
        assertNotNull(globalConfig);
        assertNotNull(globalConfig.getConfigPath());
        assertEquals(
            Environment.resolveEnvVars("${etiqet.directory}/etiqet-core/src/test/resources/config/etiqet.config.xml"),
            globalConfig.getConfigPath()
        );

        resetGlobalConfig();
    }

    @Test
    public void testExceptionWhenConfigClassAndPathSpecified() {
        try {
            new EtiqetTestRunner(DoubleConfigurationTestRun.class);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Etiqet cannot be configured with both a class and configuration file", e.getMessage());
        }
    }

    @Test
    public void testGetChildren() throws Exception {
        FeatureRunner runner = mock(FeatureRunner.class);
        Cucumber cucumber = mock(Cucumber.class);
        when(cucumber.getChildren()).thenReturn(Collections.singletonList(runner));

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationClassTestRun.class) {
            @Override
            Cucumber getCucumberDelegate(Class clazz) {
                return cucumber;
            }
        };
        List<FeatureRunner> children = testRunner.getChildren();
        assertEquals(1, children.size());
        assertEquals(runner, children.get(0));
        verify(cucumber).getChildren();
    }

    @Test
    public void testDescribeChild() throws Exception {
        Description testDescription = mock(Description.class);
        FeatureRunner child = mock(FeatureRunner.class);
        when(child.getDescription()).thenReturn(testDescription);

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationClassTestRun.class) {
            @Override
            Cucumber getCucumberDelegate(Class clazz) {
                return mock(Cucumber.class);
            }
        };

        Description description = testRunner.describeChild(child);
        assertNotNull(description);
        assertEquals(testDescription, description);
        verify(child).getDescription();
    }

    @Test
    public void testRunChild() throws Exception {
        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationClassTestRun.class) {
            @Override
            Cucumber getCucumberDelegate(Class clazz) {
                return mock(Cucumber.class);
            }
        };

        FeatureRunner runner = mock(FeatureRunner.class);
        RunNotifier notifier = mock(RunNotifier.class);
        testRunner.runChild(runner, notifier);
        verify(runner).run(eq(notifier));
    }

    @Test
    public void testRun() throws Exception {
        Cucumber cucumber = mock(Cucumber.class);

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidConfigurationClassTestRun.class) {
            @Override
            Cucumber getCucumberDelegate(Class clazz) {
                return cucumber;
            }
        };

        RunNotifier notifier = mock(RunNotifier.class);
        testRunner.run(notifier);
        verify(cucumber).run(eq(notifier));
    }
}
