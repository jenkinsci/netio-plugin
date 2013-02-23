package com.tngtech.internal.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Context {
    public static <T> T getBean(Class<T> clazz) {
        BeanFactory factory = new ClassPathXmlApplicationContext("application-context.xml");
        return factory.getBean(clazz);
    }
}
