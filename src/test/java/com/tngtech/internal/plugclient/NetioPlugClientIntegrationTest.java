package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.IntegrationTest;
import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.TelnetCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NetioPlugClientIntegrationTest {
    private NetioPlugClient client;

    @Before
    public void setUp() {
        PlugConfig plugConfig = TestPlugConfig.getIntegrationTestConfig();
        client = new TelnetCreator().getNetIoPlugClient(plugConfig);
    }

    @Test
    public void testLogin() {
        client.login();
        client.disconnect();
    }
}
