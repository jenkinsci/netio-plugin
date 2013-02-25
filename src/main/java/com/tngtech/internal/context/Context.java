package com.tngtech.internal.context;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Context extends AbstractModule {
    private static Injector injector;

    public static <T> T getBean(Class<T> clazz) {
        if (injector == null) {
            injector = Guice.createInjector(new Context());
        }
        return injector.getInstance(clazz);
    }

    protected Injector getInjector() {
        return injector;
    }

    @Override
    protected void configure() {
        // Ambiguous definitions go here
    }
}
