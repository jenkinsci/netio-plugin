package com.tngtech.internal.telnet;

import com.google.common.base.Predicate;
import com.tngtech.internal.helpers.IntegrationTest;
import com.tngtech.internal.plug.Plug;
import com.tngtech.internal.plug.PlugConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class SynchronousTelnetClientIntegrationTest {
    private SynchronousTelnetClient telnetClient;

    @Before
    public void setUp() {
        PlugConfig plugConfig = new PlugConfig("192.168.0.120", 1234, "admin", "admin", Plug.PLUG1.toString());
        telnetClient = new SynchronousTelnetClient(plugConfig);
    }

    @Test
    public void testTelnet() {
        telnetClient.connect();
        String foundMessage = telnetClient.waitForMessage(new Predicate<String>() {
            public boolean apply(String message) {
                return message.startsWith("100");
            }
        }, 1000);
        telnetClient.disconnect();

        assertTrue(foundMessage.startsWith("100 HELLO"));
    }

}
