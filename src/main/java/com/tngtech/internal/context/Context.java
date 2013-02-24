package com.tngtech.internal.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.tngtech.internal")
public class Context {
    public static <T> T getBean(Class<T> clazz) {
        BeanFactory factory = new AnnotationConfigApplicationContext(Context.class);
        return factory.getBean(clazz);
    }
}
