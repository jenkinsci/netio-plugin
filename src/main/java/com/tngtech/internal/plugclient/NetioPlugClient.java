package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class NetioPlugClient implements PlugClient {

    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 0;

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd,HH:mm:ss");

    private static class WaitForStatusCodePredicate implements Predicate<String> {
        private final Integer statusCode;

        private WaitForStatusCodePredicate(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public boolean apply(String message) {
            return message.startsWith(statusCode.toString());
        }
    }

    private PlugConfig config;

    private SynchronousTelnetClient telnetClient;

    public NetioPlugClient(SynchronousTelnetClient telnetClient, PlugConfig config) {
        this.config = config;
        this.telnetClient = telnetClient;
    }

    public void login() {
        telnetClient.connect();
        telnetClient.waitForMessage(new WaitForStatusCodePredicate(100), 1000);
        telnetClient.send(getLoginMessage());
        telnetClient.waitForMessage(new WaitForStatusCodePredicate(250), 1000);
    }

    public void enablePlugPort() {
        telnetClient.send(getPortMessage(STATE_ON));
    }

    public void disablePlugPort() {
        telnetClient.send(getPortMessage(STATE_OFF));
    }

    public void enablePlugTemporarily() {
        telnetClient.send(getSystemTimeMessage());
        String response = telnetClient.waitForMessage(new WaitForStatusCodePredicate(250), 1000);

        DateTime timeToStart = getSystemTime(response);
        DateTime timeToEnd = getTimeToEnd(timeToStart);

        telnetClient.send(getEnablePortForPeriodOfTimeMessage(timeToStart, timeToEnd));
    }

    public void disconnect() {
        telnetClient.disconnect();
    }

    private DateTime getSystemTime(String timeMessageText) {
        String timeText = stripCommandCode(timeMessageText);
        return dateTimeFormatter.parseDateTime(timeText);
    }

    protected DateTime getTimeToEnd(DateTime timeToStart) {
        // TODO not accepted by the plug
        return timeToStart.plus(new Period(0, 0, 30, 0));
    }

    private String stripCommandCode(String dateText) {
        return dateText.replaceAll("^[\\S]*", "").trim();
    }

    private String getLoginMessage() {
        return String.format("login %s %s", config.getAdminAccount(), config.getAdminPassword());
    }

    private String getPortMessage(int newState) {
        return String.format("port %d %d", config.getPlug().getPlugNumber(), newState);
    }

    private String getSystemTimeMessage() {
        return "system time";
    }

    private String getEnablePortForPeriodOfTimeMessage(DateTime timeToStart, DateTime timeToEnd) {
        String timeToStartText = timeToStart.toString(dateTimeFormatter);
        String timeToEndText = timeToEnd.toString(dateTimeFormatter);

        return String.format("port timer %d dt once %s %s 1111111", config.getPlug().getPlugNumber(), timeToStartText,
                timeToEndText);
    }
}