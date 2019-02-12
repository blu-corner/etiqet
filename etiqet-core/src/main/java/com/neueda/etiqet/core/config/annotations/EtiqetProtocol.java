package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.config.dtos.Protocol;

import java.lang.annotation.*;

/**
 * Tells Etiqet that this method should return a {@link Protocol}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface EtiqetProtocol {

    /**
     * @return name of the protocol to be accessed inside
     */
    String value();

}
