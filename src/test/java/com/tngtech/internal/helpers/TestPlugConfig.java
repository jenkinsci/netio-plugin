package com.tngtech.internal.helpers;

import com.tngtech.internal.plug.PlugConfig;

import java.util.Properties;

public class TestPlugConfig {
    public static PlugConfig getUnitTestConfig() {
        return new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1");
    }

    public static PlugConfig getIntegrationTestConfig() {
        Properties properties = PropertiesLoader.loadProperties("plug-config.properties");
        String hostName = properties.get("host.name").toString();
        int hostPort = Integer.valueOf(properties.get("host.port").toString());

        String adminAccount = properties.get("admin.account").toString();
        String adminPasword = properties.get("admin.password").toString();

        String plugName = properties.get("plug.name").toString();

        return new PlugConfig(hostName, hostPort, adminAccount, adminPasword, plugName);
    }
}