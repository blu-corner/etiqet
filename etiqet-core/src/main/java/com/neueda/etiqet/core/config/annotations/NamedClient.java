package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.config.dtos.ClientImpl;
import com.neueda.etiqet.fixture.EtiqetHandlers;

import java.lang.annotation.*;

/**
 * Tells Etiqet that this method should return a {@link ClientImpl}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface NamedClient {

    /**
     * @return the name that this client can be accessed under. Defaults to {@link EtiqetHandlers#DEFAULT_CLIENT_NAME}
     */
    String name() default EtiqetHandlers.DEFAULT_CLIENT_NAME;

    /**
     * @return the {@link EtiqetProtocol} name that this client uses
     */
    String impl();

}
