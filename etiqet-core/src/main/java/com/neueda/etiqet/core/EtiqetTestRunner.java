package com.neueda.etiqet.core;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.util.StringUtils;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.FeatureRunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EtiqetTestRunner uses {@link Cucumber} as a delegate test runner. Before instantiating the Cucumber Test Runner, we
 * first configure Etiqet based on the {@link EtiqetOptions} specified on the test class
 */
public class EtiqetTestRunner extends ParentRunner<FeatureRunner> {

    /**
     * Package that contains the Etiqet-specific fixtures
     */
    static final String FIXTURE_PACKAGE = "com.neueda.etiqet.fixture";
    private static final Logger LOG = LoggerFactory.getLogger(EtiqetTestRunner.class);
    private GlobalConfig globalConfig;

    /**
     * Instance of {@link Cucumber} that will run the unit tests with the additional Etiqet configuration required
     */
    private Cucumber cucumberDelegate;

    /**
     * {@link EtiqetOptions} used to configure this test runner
     */
    private EtiqetOptions etiqetOptions;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws IOException if there is a problem initialising Cucumber
     * @throws InitializationError if a class or resource could not be loaded for the tests
     * @throws EtiqetException if there is a problem configuring Etiqet
     */
    public EtiqetTestRunner(Class<?> clazz) throws InitializationError, IOException, EtiqetException {
        super(clazz);
        if (!clazz.isAnnotationPresent(EtiqetOptions.class)) {
            throw new EtiqetException("EtiqetTestRunner requires the @EtiqetOptions annotation");
        }

        etiqetOptions = clazz.getAnnotation(EtiqetOptions.class);
        if (StringUtils.isNullOrEmpty(etiqetOptions.configFile())
            && etiqetOptions.configClass().equals(EtiqetOptions.NullConfiguration.class)
            && !Environment.isEnvVarSet(DEFAULT_CONFIG_VARIABLE)) {
            throw new EtiqetException("EtiqetTestRunner requires one of the following configuration "
                + "options to be specified:"
                + System.lineSeparator()
                + "\t1. Specifying a configuration class via the configClass option"
                + System.lineSeparator()
                + "\t2. Specifying a configuration file using the configFile option"
                + System.lineSeparator()
                + "\t3. Setting the Environment / System variable " + DEFAULT_CONFIG_VARIABLE + " with the path "
                + "to a configuration file");
        }

        if (!etiqetOptions.configClass().equals(EtiqetOptions.NullConfiguration.class)
            && !StringUtils.isNullOrEmpty(etiqetOptions.configFile())) {
            throw new EtiqetException("Etiqet cannot be configured with both a class and configuration file");
        }

        // As GlobalConfig is a singleton, if we instantiate it here then it will be set for all test runs
        if (!etiqetOptions.configClass().equals(EtiqetOptions.NullConfiguration.class)) {
            LOG.info("Initialising EtiqetTestRunner with configuration class {}", etiqetOptions.configClass());
            globalConfig = GlobalConfig.getInstance(etiqetOptions.configClass());
        } else if (!StringUtils.isNullOrEmpty(etiqetOptions.configFile())) {
            LOG.info("Initialising EtiqetTestRunner with configuration file {}", etiqetOptions.configFile());
            globalConfig = GlobalConfig.getInstance(etiqetOptions.configFile());
        } else {
            LOG.info("Initialising EtiqetTestRunner with configuration file from System Variable {}: {}",
                DEFAULT_CONFIG_VARIABLE, Environment.resolveEnvVars(DEFAULT_CONFIG_VARIABLE));
            globalConfig = GlobalConfig.getInstance();
        }

        cucumberDelegate = getCucumberDelegate(clazz);
    }

    /**
     * Returns the Cucumber instance that is to be used as the delegate test runner.
     *
     * @param clazz the class with the @RunWith annotation
     * @return Cucumber test runner
     * @throws InitializationError if there is a problem initialising Cucumber
     * @throws IOException if a class or resource could not be loaded for the tests
     */
    Cucumber getCucumberDelegate(Class clazz) throws InitializationError, IOException {
        return new Cucumber(clazz) {

            @Override
            protected Runtime createRuntime(ResourceLoader resourceLoader,
                ClassLoader classLoader,
                RuntimeOptions runtimeOptions) {
                ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);

                if (!runtimeOptions.getGlue().contains(FIXTURE_PACKAGE)) {
                    runtimeOptions.getGlue().add(FIXTURE_PACKAGE);
                }
                Arrays.stream(etiqetOptions.additionalFixtures())
                    .filter(glue -> !runtimeOptions.getGlue().contains(glue))
                    .forEach(glue -> runtimeOptions.getGlue().add(glue));

                // ensure that list of features we provide Cucumber is unique
                Set<String> uniqueFeatures = new HashSet<>(runtimeOptions.getFeaturePaths());
                for (String feature : etiqetOptions.features()) {
                    try {
                        uniqueFeatures.add(Environment.resolveEnvVars(feature));
                    } catch (EtiqetException e) {
                        LOG.error("Unable to add feature {} because of an inaccessible environment variable",
                            feature);
                    }
                }

                runtimeOptions.getFeaturePaths().clear();
                runtimeOptions.getFeaturePaths().addAll(uniqueFeatures);

                for (String plugin : etiqetOptions.plugin()) {
                    runtimeOptions.addPlugin(plugin);
                }

                return getCucumberRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);
            }

        };
    }

    GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    /**
     * Gets an instance of {@link Runtime} for Cucumber to run the unit tests. This has been abstracted to assist with
     * unit testing of the EtiqetTestRunner
     *
     * @param resourceLoader used to load resources
     * @param classFinder used to find classes
     * @param classLoader used to load classes
     * @param runtimeOptions Runtime options specified via {@link EtiqetOptions} and {@link CucumberOptions}
     * @return Cucumber runtime
     */
    Runtime getCucumberRuntime(ResourceLoader resourceLoader,
        ClassFinder classFinder,
        ClassLoader classLoader,
        RuntimeOptions runtimeOptions) {
        return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    /**
     * {@inheritDoc}
     *
     * @return list of objects that define the children of this Runner
     */
    @Override
    protected List<FeatureRunner> getChildren() {
        return cucumberDelegate.getChildren();
    }

    /**
     * {@inheritDoc}
     *
     * @param child the child feature
     * @return a {@link Description} for {@code child}, which can be assumed to be an element of the list returned by
     * {@link ParentRunner#getChildren()}
     */
    @Override
    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    /**
     * {@inheritDoc}
     *
     * @param child child feature to be run
     * @param notifier {@link RunNotifier} to notify JUnit of running tests
     */
    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This uses the Cucumber delegate instantiated in the constructor</p>
     *
     * @param notifier {@link RunNotifier} to notify JUnit of running tests
     */
    @Override
    public void run(RunNotifier notifier) {
        cucumberDelegate.run(notifier);
    }
}
