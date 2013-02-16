package com.tngtech.internal.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadProperties(String fileName) {
        try {
            return doLoadProperties(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Properties doLoadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
        properties.load(inputStream);
        inputStream.close();

        return properties;
    }
}