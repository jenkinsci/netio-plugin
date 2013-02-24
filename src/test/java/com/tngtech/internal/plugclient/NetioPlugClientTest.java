package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.plug.Plug;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class NetioPlugClientTest {
    @Mock
    private SynchronousTelnetClient telnetClient;

    @Mock
    private PlugConfig plugConfig;

    private NetioPlugClient client;

    @Before
    public void setUp() {
        client = new NetioPlugClient(telnetClient, plugConfig);

        when(plugConfig.getAdminAccount()).thenReturn("adminAccount");
        when(plugConfig.getAdminPassword()).thenReturn("adminPassword");
        when(plugConfig.getPlug()).thenReturn(Plug.PLUG2);
    }

    @Test
    public void testLogin() {
        client.login();

        ArgumentCaptor<Predicate> captorForConnectMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForLoginMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).connect();
        inOrder.verify(telnetClient).waitForMessage(captorForConnectMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("login adminAccount adminPassword");
        inOrder.verify(telnetClient).waitForMessage(captorForLoginMessagePredicate.capture(), eq(1000));

        assertThatPredicateReturnsTrueForCode(captorForConnectMessagePredicate.getValue(), 100);
        assertThatPredicateReturnsTrueForCode(captorForLoginMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
    }

    @Test
    public void testEnablePlugPort() {
        client.enablePlugPort();

        ArgumentCaptor<Predicate> captorForEnablePlugMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("port 2 1");
        inOrder.verify(telnetClient).waitForMessage(captorForEnablePlugMessagePredicate.capture(), eq(1000));

        assertThatPredicateReturnsTrueForCode(captorForEnablePlugMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
    }

    @Test
    public void testDisablePlugPort() {
        client.disablePlugPort();

        ArgumentCaptor<Predicate> captorForDisableMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("port 2 0");
        inOrder.verify(telnetClient).waitForMessage(captorForDisableMessagePredicate.capture(), eq(1000));

        assertThatPredicateReturnsTrueForCode(captorForDisableMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
    }

    @Test
    public void testEnablePlugTemporarily() {
        when(telnetClient.waitForMessage(any(Predicate.class), anyInt())).thenReturn("250 2012/12/31,23:58:44");

        client.enablePlugPortTemporarily();

        ArgumentCaptor<Predicate> captorForSystemTimeMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForSetTimerMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForSetupPortMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("system time");
        inOrder.verify(telnetClient).waitForMessage(captorForSystemTimeMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("port timer 2 dt once 2012/12/31,23:59:45 2013/01/01,00:00:15 1111111");
        inOrder.verify(telnetClient).waitForMessage(captorForSetTimerMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("port setup 2 PLUG2 timer 0 0");
        inOrder.verify(telnetClient).waitForMessage(captorForSetupPortMessagePredicate.capture(), eq(1000));

        assertThatPredicateReturnsTrueForCode(captorForSystemTimeMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
        assertThatPredicateReturnsTrueForCode(captorForSetTimerMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
        assertThatPredicateReturnsTrueForCode(captorForSetupPortMessagePredicate.getValue(), NetioPlugClient.STATUS_OK);
    }

    @Test
    public void testDisconnect() {
        client.disconnect();

        ArgumentCaptor<Predicate> captorForQuitMessage = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("quit");
        inOrder.verify(telnetClient).waitForMessage(captorForQuitMessage.capture(), eq(1000));
        inOrder.verify(telnetClient).disconnect();

        assertThatPredicateReturnsTrueForCode(captorForQuitMessage.getValue(), NetioPlugClient.STATUS_QUIT);
    }

    private void assertThatPredicateReturnsTrueForCode(Predicate<String> predicate, int code) {
        String acceptedMessage = String.format("%d - this message should pass", code);
        String notAcceptedMessage = String.format("%d - this message should not pass", code + 1);

        assertThat(predicate.apply(acceptedMessage), is(true));
        assertThat(predicate.apply(notAcceptedMessage), is(false));
    }
}