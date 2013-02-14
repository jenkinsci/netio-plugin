package com.tngtech.internal.plugclient;

import com.tngtech.internal.plug.Plug;
import com.tngtech.internal.plug.PlugConfig;
import org.junit.Before;
import org.junit.Test;

public class NetioPlugClientTest {
    private NetioPlugClient client;

    @Before
    public void setUp() {
        PlugConfig plugConfig = new PlugConfig("192.168.0.120", 1234, "admin", "admin", Plug.PLUG1.toString());
        client = new NetioPlugClient(plugConfig);
    }

    @Test
    public void testLogin() {
        client.login();
        client.disconnect();
    }
}
