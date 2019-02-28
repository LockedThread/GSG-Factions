package com.gameservergroup.gsgcore.plugin.processor;

import org.bukkit.plugin.PluginLoadOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used inspiration from Lucko's helper repo for plugin yml processing
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GSGPlugin {

    String name();

    String version() default "1.0-SNAPSHOT";

    String description() default "A GSG Plugin using the GSGCore Module system";

    PluginLoadOrder load() default PluginLoadOrder.POSTWORLD;

    String[] authors() default {};

    String website() default "www.gameservergroup.com";

    Dependency[] depends() default {};
}