package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class NetioPlugClient implements PlugClient {
    public static final int DEFAULT_TIMEOUT = 1000;

    private static class WaitForStatusCodePredicate implements Predicate<String> {
        private final Integer statusCode;

        private WaitForStatusCodePredicate(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public boolean apply(String message) {
            return message.startsWith(statusCode.toString());
        }
    }

    private final NetioPlugMessages messages;

    private final SynchronousTelnetClient telnetClient;

    private final PlugConfig config;

    public NetioPlugClient(SynchronousTelnetClient telnetClient, NetioPlugMessages messages, PlugConfig config) {
        this.telnetClient = telnetClient;
        this.messages = messages;
        this.config = config;
    }

    public void login() {
        telnetClient.connect();
        String welcomeMessageText = waitForAcknowledge(NetioPlugMessages.STATUS_LOGIN);

        sendAndWaitForAcknowledge(messages.getLoginMessage(config.getAdminAccount(), config.getAdminPassword(), welcomeMessageText),
                NetioPlugMessages.STATUS_OK);
    }

    public void enablePlugPort() {
        sendAndWaitForAcknowledge(messages.getPortEnableDisableMessage(config.getPlug(), NetioPlugMessages.PLUG_ON), NetioPlugMessages.STATUS_OK);
    }

    public void disablePlugPort() {
        sendAndWaitForAcknowledge(messages.getPortEnableDisableMessage(config.getPlug(), NetioPlugMessages.PLUG_OFF), NetioPlugMessages.STATUS_OK);
    }

    public boolean shouldEnable() {
        String response = sendAndWaitForAcknowledge(messages.getTimerMessage(config.getPlug()), NetioPlugMessages.STATUS_OK);

        if (!messages.isTimerSet(response)) {
            return true;
        }

        //DateTime startTime = getStartTimeFrom(response);
        //DateTime endTime = getEndTimeFrom(response);

        return false;
    }

    public void enablePlugPortTemporarily() {
        String response = sendAndWaitForAcknowledge(messages.getSystemTimeMessage(), NetioPlugMessages.STATUS_OK);

        DateTime timeToStart = getTimeToStart(messages.getSystemTime(response), config.getDelaySeconds());
        DateTime timeToEnd = getTimeToEnd(timeToStart, config.getActivationDurationSeconds());

        sendAndWaitForAcknowledge(messages.getEnablePortForPeriodOfTimeMessage(config.getPlug(), timeToStart, timeToEnd), NetioPlugMessages.STATUS_OK);
        sendAndWaitForAcknowledge(messages.getTimerEnableMessage(config.getPlug()), NetioPlugMessages.STATUS_OK);
    }

    public void disconnect() {
        sendAndWaitForAcknowledge(messages.getQuitMessage(), NetioPlugMessages.STATUS_QUIT);
        telnetClient.disconnect();
    }

    private String sendAndWaitForAcknowledge(String message, int statusCode) {
        telnetClient.send(message);
        return waitForAcknowledge(statusCode);
    }

    private String waitForAcknowledge(int statusCode) {
        return telnetClient.waitForMessage(new WaitForStatusCodePredicate(statusCode), DEFAULT_TIMEOUT);
    }

    private DateTime getTimeToStart(DateTime systemTime, int delaySeconds) {
        // The plug does not allow a start time that less than one minute away
        return systemTime.plus(new Period(0, 0, delaySeconds, 0));
    }

    private DateTime getTimeToEnd(DateTime timeToStart, int activationDurationSeconds) {
        return timeToStart.plus(new Period(0, 0, activationDurationSeconds, 0));
    }
}