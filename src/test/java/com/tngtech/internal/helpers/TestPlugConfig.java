package com.tngtech.internal.helpers;

import com.tngtech.internal.plug.PlugConfig;

import java.util.Properties;

public class TestPlugConfig {
    public static PlugConfig getUnitTestConfig() {
        return new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1", 60, 30);
    }

    public static PlugConfig getIntegrationTestConfig() {
        Properties properties = PropertiesLoader.loadProperties("plug-config.properties");
        String hostName = properties.get("host.name").toString();
        int hostPort = Integer.valueOf(properties.get("host.port").toString());

        String adminAccount = properties.get("admin.account").toString();
        String adminPasword = properties.get("admin.password").toString();

        String plugName = properties.get("plug.name").toString();

        int delaySeconds = Integer.valueOf(properties.get("time.delay").toString());
        int activationDurationSeconds = Integer.valueOf(properties.get("time.activation.duration").toString());

        return new PlugConfig(hostName, hostPort, adminAccount, adminPasword, plugName, delaySeconds, activationDurationSeconds);
    }
}