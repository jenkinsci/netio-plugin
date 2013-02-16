package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.IntegrationTest;
import com.tngtech.internal.plug.Plug;
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
        PlugConfig plugConfig = new PlugConfig("192.168.0.120", 1234, "admin", "admin", Plug.PLUG1.toString());
        client = new TelnetCreator().getNetIoPlugClient(plugConfig);
    }

    @Test
    public void testLogin() {
        client.login();
        client.disconnect();
    }
}
