package com.tngtech.internal.telnet;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SynchronousTelnetClient.class)
public class SynchronousTelnetClientTest {

    private class SendNotificationAnswer implements Answer<Void> {
        private final List<String> notificationMessages;
        private int currentIndex;

        public SendNotificationAnswer(String... notificationMessages) {
            this.notificationMessages = Lists.newArrayList(notificationMessages);
            this.currentIndex = 0;
        }

        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
            if (currentIndex < notificationMessages.size() && notificationMessages.get(currentIndex) != null) {
                asynchronousTelnetClient.sendNotification(notificationMessages.get(currentIndex));
            }
            currentIndex++;
            return null;
        }
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Predicate<String> waitForMatchingStringPredicate;

    private AsynchronousTelnetClient asynchronousTelnetClient;

    private SynchronousTelnetClient synchronousTelnetClient;

    @Before
    public void setUp() {
        asynchronousTelnetClient = spy(new AsynchronousTelnetClient(null, null));
        synchronousTelnetClient = new SynchronousTelnetClient(asynchronousTelnetClient);

        PowerMockito.mockStatic(Thread.class);

        doNothing().when(asynchronousTelnetClient).connect();
        doNothing().when(asynchronousTelnetClient).disconnect();
        doNothing().when(asynchronousTelnetClient).send(anyString());

        waitForMatchingStringPredicate = spy(new Predicate<String>() {
            public boolean apply(String string) {
                return string.equals("matchingString");
            }
        });
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
    public void whenMessageDoesNotArriveAnErrorShouldBeThrown() throws InterruptedException {
        PowerMockito.doNothing().when(Thread.class);
        Thread.sleep(anyInt());

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The expected response did not arrive");

        synchronousTelnetClient.waitForMessage(waitForMatchingStringPredicate, 1000);
    }

    @Test
    public void testWaitForMessage() throws InterruptedException {
        PowerMockito.doAnswer(new SendNotificationAnswer("nonMatchingString", "matchingString")).when(Thread.class);
        Thread.sleep(anyInt());

        synchronousTelnetClient.waitForMessage(waitForMatchingStringPredicate, 1000);

        verify(waitForMatchingStringPredicate).apply("nonMatchingString");
        verify(waitForMatchingStringPredicate).apply("matchingString");

        PowerMockito.verifyStatic(times(2));
        Thread.sleep(SynchronousTelnetClient.TIME_TO_SLEEP);
    }

    @Test
    public void whenWaitingIOExceptionsShouldBeRethrown() throws InterruptedException {
        PowerMockito.doThrow(new InterruptedException()).when(Thread.class);
        Thread.sleep(anyInt());

        expectedException.expect(IllegalStateException.class);

        synchronousTelnetClient.waitForMessage(waitForMatchingStringPredicate, 1000);
    }
}