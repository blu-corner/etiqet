package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.config.dtos.ServerImpl;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.fixture.EtiqetHandlers;

import java.lang.annotation.*;

/**
 * Tells Etiqet that this method should return a {@link ServerImpl}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface NamedServer {

    /**
     * @return the name of the server. Defaults to {@link EtiqetHandlers#DEFAULT_SERVER_NAME}
     * @see EtiqetHandlers#DEFAULT_SERVER_NAME
     */
    String name() default EtiqetHandlers.DEFAULT_SERVER_NAME;

    /**
     * @return class that implements the server
     */
    Class<? extends Server> impl();

    /**
     * @return path to the configuration file for the server
     */
    String config();
}
