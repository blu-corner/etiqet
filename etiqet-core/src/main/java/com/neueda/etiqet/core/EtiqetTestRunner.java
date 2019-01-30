package com.neueda.etiqet.core;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.util.StringUtils;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.FeatureRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;

/**
 * EtiqetTestRunner uses {@link Cucumber} as a delegate test runner. Before instantiating the Cucumber Test Runner,
 * we first configure the Etiqet Configuration based on the {@link EtiqetOptions} specified on the test class
 */
public class EtiqetTestRunner extends ParentRunner<FeatureRunner> {

    static final String FIXTURE_PACKAGE = "com.neueda.etiqet.fixture";
    private static final Logger LOG = LogManager.getLogger(EtiqetTestRunner.class);
    private Cucumber cucumberDelegate;
    private EtiqetOptions etiqetOptions;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws IOException         if there is a problem initialising Cucumber
     * @throws InitializationError if a class or resource could not be loaded for the tests
     * @throws EtiqetException     if there is a problem configuring Etiqet
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

        // As GlobalConfig is a singleton, if we instantiate it here then it will be set for all test runs
        GlobalConfig globalConfig;
        if (!etiqetOptions.configClass().equals(EtiqetOptions.NullConfiguration.class)) {
            LOG.info("Initialising EtiqetTestRunner with configuration class " + etiqetOptions.configClass());
            globalConfig = GlobalConfig.getInstance(etiqetOptions.configClass());
        } else if (!StringUtils.isNullOrEmpty(etiqetOptions.configFile())) {
            LOG.info("Initialising EtiqetTestRunner with configuration file " + etiqetOptions.configFile());
            globalConfig = GlobalConfig.getInstance(etiqetOptions.configFile());
        } else {
            LOG.info("Initialising EtiqetTestRunner with configuration file from System Variable " +
                DEFAULT_CONFIG_VARIABLE + ": " + Environment.resolveEnvVars(DEFAULT_CONFIG_VARIABLE));
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
     * @throws IOException         if a class or resource could not be loaded for the tests
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
                Set<String> uniqueFeatures = new HashSet<>();
                uniqueFeatures.addAll(Arrays.asList(etiqetOptions.features()));
                uniqueFeatures.addAll(runtimeOptions.getFeaturePaths());

                runtimeOptions.getFeaturePaths().clear();
                runtimeOptions.getFeaturePaths().addAll(uniqueFeatures);

                return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
            }

        };
    }

    @Override
    protected List<FeatureRunner> getChildren() {
        return cucumberDelegate.getChildren();
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {
        cucumberDelegate.run(notifier);
    }
}
