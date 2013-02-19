package com.tngtech.internal.telnet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SynchronousTelnetClientTest {

    @Mock
    private AsynchronousTelnetClient asynchronousTelnetClient;

    private SynchronousTelnetClient synchronousTelnetClient;

    @Before
    public void setUp() {
        synchronousTelnetClient = new SynchronousTelnetClient(asynchronousTelnetClient);
    }

    @Test
    public void testConnect() {
        synchronousTelnetClient.connect();
        verify(asynchronousTelnetClient).connect();
    }

    @Test
    public void testDisconnect() {
        synchronousTelnetClient.disconnect();
        verify(asynchronousTelnetClient).disconnect();
    }

    @Test
    public void testSend() {
        synchronousTelnetClient.send("message");
        verify(asynchronousTelnetClient).send("message");
    }

    @Test
    public void testWaitForMessage() {

    }

}