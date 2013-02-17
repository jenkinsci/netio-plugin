package com.tngtech.internal.telnet;

import com.google.common.collect.Lists;
import com.tngtech.internal.helpers.TestPlugConfig;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.AsynchronousTelnetClient.State;
import com.tngtech.internal.telnet.notifications.NotificationHandler;
import com.tngtech.internal.wrappers.Scanner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AsynchronousTelnetClient.class)
public class AsynchronousTelnetClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        when(telnetCreator.getSocketReader(socket)).thenReturn(scanner);
        when(telnetCreator.getSocketWriter(socket)).thenReturn(printWriter);
        when(telnetCreator.getThread(any(Runnable.class))).thenReturn(readerThread);
    }

    @Test
    public void testConnect() {

        telnetClient.connect();

        verify(telnetCreator).getSocket(plugConfig.getHostName(), plugConfig.getHostPort());
        verify(telnetCreator).getSocketReader(socket);
        verify(telnetCreator).getSocketWriter(socket);
        verify(telnetCreator).getThread(telnetClient);

        verify(readerThread).start();
    }

    @Test
    public void whenConnectingToAConnectedClientAnErrorShouldBeThrown() {
        telnetClient.connect();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client is already connected");

        telnetClient.connect();

        // Only one invocation
        verify(telnetCreator, times(1)).getSocket(anyString(), anyInt());
    }

    @Test
    public void whenConnectingToADisconnectedClientAnErrorShouldBeThrown() throws IOException {
        telnetClient.connect();
        telnetClient.disconnect();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client has already been disconnected");

        telnetClient.connect();

        // Only one invocation
        verify(telnetCreator, times(1)).getSocket(anyString(), anyInt());
    }

    @Test
    public void testDisconnect() throws IOException {
        telnetClient.connect();
        telnetClient.disconnect();

        verify(socket).close();
        verify(scanner).close();

        verify(printWriter).close();
        verify(readerThread).interrupt();
    }

    @Test
    public void whenDisconnectingFromAClientNotConnectedAnErrorShouldBeThrown() throws IOException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client has not yet been connected");

        telnetClient.disconnect();
        verify(socket, never()).close();
    }

    @Test
    public void whenDisconnectingFromADisconnectedClientAnErrorShouldBeThrown() throws IOException {
        telnetClient.connect();
        telnetClient.disconnect();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client has already been disconnected");

        telnetClient.disconnect();

        // Only one invocation
        verify(socket, times(1)).close();
    }

    @Test
    public void ioExceptionsShouldBeCastedWhenDisconnecting() throws IOException {
        doThrow(IOException.class).when(socket).close();

        telnetClient.connect();

        expectedException.expect(IllegalStateException.class);

        telnetClient.disconnect();
        assertThat(telnetClient.getCurrentState(), is(State.DISCONNECTED));
    }

    @Test
    public void testState() {
        assertThat(telnetClient.getCurrentState(), is(State.NOT_CONNECTED));

        telnetClient.connect();
        assertThat(telnetClient.getCurrentState(), is(State.CONNECTED));

        telnetClient.disconnect();
        assertThat(telnetClient.getCurrentState(), is(State.DISCONNECTED));
    }

    @Test
    public void testSend() {
        telnetClient.connect();
        telnetClient.send("text");

        verify(printWriter).println("text");
    }

    @Test
    public void sendShouldThrowAnErrorIfClientIsNotConnected() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client has not yet been connected");

        telnetClient.send("text");
    }

    @Test
    public void sendShouldThrowAnErrorIfClientIsAlreadyDisconnected() {
        telnetClient.connect();
        telnetClient.disconnect();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The client has already been disconnected");

        telnetClient.send("text");
    }

    @Test
    public void receivingALineShouldDoNothingIfNoListenerAreInstalled() {
        telnetClient.connect();

        when(scanner.hasNextLine()).thenReturn(true).thenReturn(false);
        when(scanner.nextLine()).thenReturn("text");

        telnetClient.run();
    }

    @Test
    public void testEventHandlers() {
        final List<String> messagesReceived = Lists.newArrayList();
        NotificationHandler notificationHandler = new NotificationHandler() {
            public void getNotification(String message) {
                messagesReceived.add(message);
            }
        };

        telnetClient.connect();
        telnetClient.addNotificationHandler(notificationHandler);

        when(scanner.hasNextLine()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(scanner.nextLine()).thenReturn("text1").thenReturn("text2");

        telnetClient.run();

        assertThat(messagesReceived.size(), is(2));
        assertThat(messagesReceived.get(0), is("text1"));
        assertThat(messagesReceived.get(1), is("text2"));
    }

    @Test
    public void whenRemovingAnEventHandlerNoMoreEventsShouldBeTriggered() {
        final List<String> messagesReceived = Lists.newArrayList();
        NotificationHandler notificationHandler = new NotificationHandler() {
            public void getNotification(String message) {
                messagesReceived.add(message);
            }
        };

        telnetClient.connect();

        telnetClient.addNotificationHandler(notificationHandler);
        telnetClient.removeNotificationHandler(notificationHandler);

        when(scanner.hasNextLine()).thenReturn(true).thenReturn(false);
        when(scanner.nextLine()).thenReturn("text3");

        telnetClient.run();

        assertThat(messagesReceived.size(), is(0));
    }
}