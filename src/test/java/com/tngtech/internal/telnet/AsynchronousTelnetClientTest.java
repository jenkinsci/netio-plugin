package com.tngtech.internal.telnet;

import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plug.PlugConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class AsynchronousTelnetClientTest {
    private PlugConfig plugConfig;

    @Mock
    private TelnetCreator telnetCreator;

    @Mock
    private Socket socket;

    @Mock
    private Scanner scanner;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private Thread readerThread;

    private AsynchronousTelnetClient telnetClient;

    @Before
    public void setUp() {
        plugConfig = TestPlugConfig.getUnitTestConfig();
        telnetClient = new AsynchronousTelnetClient(telnetCreator, plugConfig);


        when(telnetCreator.getSocket(anyString(), anyInt())).thenReturn(socket);
        when(telnetCreator.getSocketScanner(socket)).thenReturn(scanner);
        when(telnetCreator.getSocketWriter(socket)).thenReturn(printWriter);
        when(telnetCreator.getThread(any(Runnable.class))).thenReturn(readerThread);
    }

    @Test
    public void testConnect() {
        telnetClient.connect();

        verify(telnetCreator).getSocket(plugConfig.getHostName(), plugConfig.getHostPort());
    }
}