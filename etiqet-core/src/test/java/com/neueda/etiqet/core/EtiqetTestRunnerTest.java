package com.neueda.etiqet.core;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.testing.EmptyOptionsTestRun;
import com.neueda.etiqet.core.testing.NoOptionsTestRun;
import com.neueda.etiqet.core.testing.ValidTestRun;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.junit.FeatureRunner;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.Collections;
import java.util.List;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EtiqetTestRunnerTest {

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
    public void testGetChildren() throws Exception {
        FeatureRunner runner = mock(FeatureRunner.class);
        Cucumber cucumber = mock(Cucumber.class);
        when(cucumber.getChildren()).thenReturn(Collections.singletonList(runner));

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidTestRun.class) {
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

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidTestRun.class) {
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
        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidTestRun.class) {
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

        EtiqetTestRunner testRunner = new EtiqetTestRunner(ValidTestRun.class) {
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
