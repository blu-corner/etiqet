package com.neueda.etiqet.core.config.annotations;

import java.lang.annotation.*;

/**
 * Tells Etiqet that this method should return a ProtocolConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Protocol {

    /**
     * @return name of the protocol to be accessed inside
     */
    String value();

}
