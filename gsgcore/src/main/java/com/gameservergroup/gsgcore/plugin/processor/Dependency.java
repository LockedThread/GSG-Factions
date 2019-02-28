package com.gameservergroup.gsgcore.plugin.processor;

/**
 * Used inspiration from Lucko's helper repo for plugin yml processing
 */

public @interface Dependency {

    String pluginName();

    boolean softDependency() default false;

}
