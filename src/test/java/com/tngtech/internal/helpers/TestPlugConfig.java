package com.tngtech.internal.helpers;

import com.tngtech.internal.plug.PlugConfig;

public class TestPlugConfig {
    public static PlugConfig getUnitTestConfig() {
        return new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1");
    }

    public static PlugConfig getIntegrationTestConfig() {
        return new PlugConfig("192.168.0.120", 1234, "admin", "admin", "PLUG1");
    }
}