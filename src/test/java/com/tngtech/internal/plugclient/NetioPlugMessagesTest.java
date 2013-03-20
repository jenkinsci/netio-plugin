package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.HashHelper;
import com.tngtech.internal.plug.Plug;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NetioPlugMessagesTest {

    @Mock
    private HashHelper hashHelper;

    private NetioPlugMessages messages;

    @Before
    public void setUp() {
        messages = new NetioPlugMessages(hashHelper);
        when(hashHelper.hashString(anyString(), anyString())).thenReturn("encryptedPassword");
    }

    @Test
    public void testGetLoginMessage() {
        String loginMessage = messages.getLoginMessage("userName", "password", "100 HELLO valueToHash - KSHELL V1.3");

        assertThat(loginMessage, is("clogin userName encryptedPassword"));
        verify(hashHelper, times(1)).hashString("userNamepasswordvalueToHash", "MD5");
    }

    @Test
    public void testGetSystemTimeMessage() {
        assertThat(messages.getSystemTimeMessage(), is("system time"));
    }

    @Test
    public void testGetTimerMessage() {
        assertThat(messages.getTimerMessage(Plug.PLUG1), is("port timer 1 dt"));
    }

    @Test
    public void testGetPortEnableDisableMessage() {
        assertThat(messages.getPortEnableDisableMessage(Plug.PLUG3, NetioPlugMessages.PLUG_OFF), is("port 3 0"));
    }

    @Test
    public void testGetEnablePortForPeriodOfTimeMessage() {
        DateTime startTime = new DateTime("2013-01-02T19:34:00");
        DateTime endTime = new DateTime("2013-01-02T19:35:00");

        assertThat(messages.getEnablePortForPeriodOfTimeMessage(Plug.PLUG2, startTime, endTime),
                is("port timer 2 dt once 2013/01/02,19:34:00 2013/01/02,19:35:00 1111111"));
    }

    @Test
    public void testGetTimerEnableMessage() {
        assertThat(messages.getTimerEnableMessage(Plug.PLUG4), is("port setup 4 PLUG4 timer 0 0"));
    }

    @Test
    public void testGetQuitMessage() {
        assertThat(messages.getQuitMessage(), is("quit"));
    }

    @Test
    public void testGetSystemTime() {
        assertThat(messages.getSystemTime("250 2013/01/02,19:34:39"), is(new DateTime("2013-01-02T19:34:39")));
    }

    @Test
    public void testIsTimerSet() {
        assertThat(messages.isTimerSet("250 once 19:14:56 19:15:26 1111111"), is(false));
        assertThat(messages.isTimerSet("250 timer 08:00:00 17:30:00 1111111"), is(true));
    }

    @Test
    public void testGetTimeForTimerMessage() {
        String responseText = "250 once 2013/01/02,20:13:03 2013/01/02,20:13:33 1111111";

        assertThat(messages.getStartTimeFromTimerMessage(responseText), is(new DateTime("2013-01-02T20:13:03")));
        assertThat(messages.getEndTimeFromTimerMessage(responseText), is(new DateTime("2013-01-02T20:13:33")));
    }
}