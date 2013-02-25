package com.tngtech.internal.context;

import com.google.inject.Inject;

import static com.google.common.base.Preconditions.checkState;

public class ValueBuilder {
    private Context context;

    @Inject
    public ValueBuilder(Context context) {
        this.context = context;
    }

    public <T> T getBean(Class<T> clazz) {
        checkState(context != null && context.getInjector() != null, "This class must be instantiated using DI");
        return context.getInjector().getInstance(clazz);
    }
}
