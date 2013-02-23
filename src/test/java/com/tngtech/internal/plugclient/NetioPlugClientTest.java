package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
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
    }

    @Test
    public void testDisconnect() {
        client.disconnect();
        verify(telnetClient).disconnect();
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
        assertThatPredicateReturnsTrueForCode(captorForLoginMessagePredicate.getValue(), 250);
    }

    private void assertThatPredicateReturnsTrueForCode(Predicate<String> predicate, int code) {
        String acceptedMessage = String.format("%d - this message should pass", code);
        String notAcceptedMessage = String.format("%d - this message should not pass", code + 1);

        assertThat(predicate.apply(acceptedMessage), is(true));
        assertThat(predicate.apply(notAcceptedMessage), is(false));
    }
}