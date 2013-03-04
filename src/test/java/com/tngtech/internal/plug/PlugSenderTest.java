package com.tngtech.internal.plug;

import com.tngtech.internal.context.Context;
import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plugclient.PlugClient;
import com.tngtech.internal.plugclient.PlugClientCreator;
import hudson.model.BuildListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.PrintStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PlugSenderTest {
    @Mock
    private BuildListener buildListener;

    @Mock
    private PrintStream logger;

    @Mock
    private PlugClientCreator plugClientCreator;

    @Mock
    private PlugClient plugClient;

    private PlugConfig plugConfig;

    private PlugSender plugSender;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Context.class);
        when(Context.getBean(PlugClientCreator.class)).thenReturn(plugClientCreator);
        when(plugClientCreator.withPlugConfig(any(PlugConfig.class))).thenReturn(plugClientCreator);
        when(plugClientCreator.createClient()).thenReturn(plugClient);

        when(buildListener.getLogger()).thenReturn(logger);

        plugConfig = TestPlugConfig.getUnitTestConfig();
        plugSender = new PlugSender();
    }

    @Test
    public void testSend() {
        plugSender.send(buildListener, plugConfig);
        verify(logger).println("Using connection to adminAccount:adminPassword@hostName:80 for plug number 1, delaying 60 seconds, then activating for 30 seconds");

        verifyStatic();
        Context.getBean(PlugClientCreator.class);

        verify(plugClientCreator).withPlugConfig(plugConfig);
        verify(plugClientCreator).createClient();

        verify(plugClient).login();
        verify(plugClient).enablePlugPortTemporarily();
        verify(plugClient).disconnect();
    }
}