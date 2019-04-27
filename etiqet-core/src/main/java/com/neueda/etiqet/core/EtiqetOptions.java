package com.neueda.etiqet.core;

import cucumber.api.CucumberOptions;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to configure Etiqet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface EtiqetOptions {

    /**
     * @return a path to the Etiqet Configuration file
     */
    String configFile() default "";

    /**
     * @return the class used to configure Etiqet
     */
    Class<?> configClass() default NullConfiguration.class;

    /**
     * @return the paths to the feature(s)
     */
    String[] features() default {};

    /**
     * @return the paths to any addition Fixtures to configure test steps
     */
    String[] additionalFixtures() default {};

    /**
     * @return what plugins(s) to pass on to Cucumber
     * @see CucumberOptions#plugin()
     */
    String[] plugin() default {};

    /**
     * Interface that represents that {@link #configClass()} has not been specified
     */
    interface NullConfiguration {

    }

}
