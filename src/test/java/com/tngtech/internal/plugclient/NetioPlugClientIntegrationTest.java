package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.HashHelper;
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
        client = new NetioPlugClient(new TelnetClientCreator().getSynchronousTelnetClient(plugConfig), new NetioPlugMessages(new HashHelper()), plugConfig);
    }

    @Test
    public void testManuallyEnablingThePlugPort() throws InterruptedException {
        client.login();

        client.enablePlugPort();
        Thread.sleep(2000);
        client.disablePlugPort();

        client.disconnect();
    }

    @Test
    public void testEnableClientTemporarily() {
        client.login();
        client.enablePlugPortTemporarily();
        client.disconnect();
    }
}
