package com.tngtech.internal.telnet;

import com.tngtech.internal.plug.PlugConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AsynchronousTelnetClientTest {
    @Mock
    private PlugConfig plugConfig;

    @Mock
    private TelnetCreator telnetCreator;

    private AsynchronousTelnetClient telnetClient;

    @Before
    public void setUp() {
        telnetClient = new AsynchronousTelnetClient(telnetCreator, plugConfig);
    }

    @Test
    public void testBla() {

    }
}