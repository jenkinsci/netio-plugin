package com.tngtech.internal.plug;

import hudson.model.BuildListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlugSenderTest {

    private PlugSender plugSender;

    @Mock
    private BuildListener buildListener;

    @Mock
    private PrintStream logger;

    private PlugConfig plugConfig;

    @Before
    public void setUp() {
        plugConfig = new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1");
        plugSender = new PlugSender();

        when(buildListener.getLogger()).thenReturn(logger);
    }

    @Test
    public void testSend() {
        plugSender.send(buildListener, plugConfig);
        verify(logger).println("Using connection to adminAccount:adminPassword@hostName:80 for plug number 1");
    }
}