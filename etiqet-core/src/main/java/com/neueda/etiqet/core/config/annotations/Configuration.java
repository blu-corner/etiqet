package com.neueda.etiqet.core.config.annotations;

import java.lang.annotation.*;

/**
 * Tells Etiqet to scan a class for {@link EtiqetProtocol}s, {@link NamedClient}s and {@link NamedServer}s
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Configuration {
}
