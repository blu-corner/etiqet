package com.neueda.etiqet.core.config.annotations;

import java.lang.annotation.*;

/**
 * Tells Etiqet to scan a class for Protocols, Clients and Servers
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Configuration {
}
