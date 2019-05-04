package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.config.dtos.Protocol;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
