package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.config.dtos.ServerImpl;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.fixture.EtiqetHandlers;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
