package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.fixture.EtiqetHandlers;

import java.lang.annotation.*;

/**
 * Tells Etiqet that this method should return a {@link com.neueda.etiqet.core.config.dtos.ClientImpl}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Client {

    /**
     * @return the name that this client can be accessed under. Defaults to {@link EtiqetHandlers#DEFAULT_CLIENT_NAME}
     */
    String name() default EtiqetHandlers.DEFAULT_CLIENT_NAME;

    /**
     * @return the {@link Protocol} name that this client uses
     */
    String impl();

}
