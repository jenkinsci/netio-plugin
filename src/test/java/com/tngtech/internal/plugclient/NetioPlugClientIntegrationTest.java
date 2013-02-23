package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.IntegrationTest;
import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.TelnetClientCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NetioPlugClientIntegrationTest {
    private NetioPlugClient client;

    @Before
    public void setUp() {
        PlugConfig plugConfig = TestPlugConfig.getIntegrationTestConfig();
        client = new NetioPlugClient(new TelnetClientCreator().getSynchronousTelnetClient(plugConfig), plugConfig);
    }

    @Test
    public void testLogin() throws InterruptedException {
        client.login();

        client.enablePlugPort();
        Thread.sleep(2000);
        client.disablePlugPort();

        client.disconnect();
    }
}
