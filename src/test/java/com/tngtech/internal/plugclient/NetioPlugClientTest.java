package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plug.Plug;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import org.joda.time.DateTime;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class NetioPlugClientTest {
    @Mock
    private SynchronousTelnetClient telnetClient;

    @Mock
    private NetioPlugMessages messages;

    private PlugConfig plugConfig;

    private NetioPlugClient client;

    @Before
    public void setUp() {
        plugConfig = TestPlugConfig.getUnitTestConfig();
        client = new NetioPlugClient(telnetClient, messages, plugConfig);

        mockMessages();
    }

    private void mockMessages() {
        when(messages.getLoginMessage(anyString(), anyString(), anyString())).thenReturn("loginMessage");
        when(messages.getSystemTimeMessage()).thenReturn("systemTimeMessage");
        when(messages.getTimerMessage(any(Plug.class))).thenReturn("timerMessage");
        when(messages.getPortEnableDisableMessage(any(Plug.class), anyInt())).thenReturn("portEnableDisableMessage");
        when(messages.getEnablePortForPeriodOfTimeMessage(any(Plug.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn("enablePortForPeriodOfTimeMessage");
        when(messages.getTimerEnableMessage(any(Plug.class))).thenReturn("timerEnableMessage");
        when(messages.getQuitMessage()).thenReturn("quitMessage");

        when(messages.getSystemTime(anyString())).thenReturn(new DateTime("2013-01-02T19:34:00"));
    }

    @Test
    public void testLogin() {
        when(telnetClient.waitForMessage(any(Predicate.class), anyInt())).thenReturn("welcomeMessage");

        client.login();

        ArgumentCaptor<Predicate> captorForConnectMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForLoginMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).connect();
        inOrder.verify(telnetClient).waitForMessage(captorForConnectMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("loginMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForLoginMessagePredicate.capture(), eq(1000));

        verify(messages).getLoginMessage("adminAccount", "adminPassword", "welcomeMessage");

        assertThatPredicateReturnsTrueForCode(captorForConnectMessagePredicate.getValue(), NetioPlugMessages.STATUS_LOGIN);
        assertThatPredicateReturnsTrueForCode(captorForLoginMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
    }

    @Test
    public void testEnablePlugPort() {
        client.enablePlugPort();

        ArgumentCaptor<Predicate> captorForEnablePlugMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("portEnableDisableMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForEnablePlugMessagePredicate.capture(), eq(1000));

        verify(messages).getPortEnableDisableMessage(Plug.PLUG1, NetioPlugMessages.PLUG_ON);

        assertThatPredicateReturnsTrueForCode(captorForEnablePlugMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
    }

    @Test
    public void testDisablePlugPort() {
        client.disablePlugPort();

        ArgumentCaptor<Predicate> captorForDisableMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("portEnableDisableMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForDisableMessagePredicate.capture(), eq(1000));

        verify(messages).getPortEnableDisableMessage(Plug.PLUG1, NetioPlugMessages.PLUG_OFF);

        assertThatPredicateReturnsTrueForCode(captorForDisableMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
    }

    @Test
    public void testShouldEnable() {
        boolean shouldBeEnabled = client.shouldEnable();
        // TODO test 
    }

    @Test
    public void testEnablePlugTemporarily() {
        when(telnetClient.waitForMessage(any(Predicate.class), anyInt())).thenReturn("systemTimeResponseMessage");

        client.enablePlugPortTemporarily();

        ArgumentCaptor<Predicate> captorForSystemTimeMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForSetTimerMessagePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> captorForSetupPortMessagePredicate = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("systemTimeMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForSystemTimeMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("enablePortForPeriodOfTimeMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForSetTimerMessagePredicate.capture(), eq(1000));
        inOrder.verify(telnetClient).send("timerEnableMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForSetupPortMessagePredicate.capture(), eq(1000));

        verify(messages).getSystemTimeMessage();
        verify(messages).getEnablePortForPeriodOfTimeMessage(Plug.PLUG1, new DateTime("2013-01-02T19:35:00"), new DateTime("2013-01-02T19:35:30"));
        verify(messages).getTimerEnableMessage(Plug.PLUG1);

        assertThatPredicateReturnsTrueForCode(captorForSystemTimeMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
        assertThatPredicateReturnsTrueForCode(captorForSetTimerMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
        assertThatPredicateReturnsTrueForCode(captorForSetupPortMessagePredicate.getValue(), NetioPlugMessages.STATUS_OK);
    }

    @Test
    public void testDisconnect() {
        client.disconnect();

        ArgumentCaptor<Predicate> captorForQuitMessage = ArgumentCaptor.forClass(Predicate.class);

        InOrder inOrder = inOrder(telnetClient);
        inOrder.verify(telnetClient).send("quitMessage");
        inOrder.verify(telnetClient).waitForMessage(captorForQuitMessage.capture(), eq(1000));
        inOrder.verify(telnetClient).disconnect();

        verify(messages).getQuitMessage();

        assertThatPredicateReturnsTrueForCode(captorForQuitMessage.getValue(), NetioPlugMessages.STATUS_QUIT);
    }

    private void assertThatPredicateReturnsTrueForCode(Predicate<String> predicate, int code) {
        String acceptedMessage = String.format("%d - this message should pass", code);
        String notAcceptedMessage = String.format("%d - this message should not pass", code + 1);

        assertThat(predicate.apply(acceptedMessage), is(true));
        assertThat(predicate.apply(notAcceptedMessage), is(false));
    }
}