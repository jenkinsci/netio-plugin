package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.helpers.HashHelper;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class NetioPlugClient implements PlugClient {
    public static final int DEFAULT_TIMEOUT = 1000;

    public static final int STATUS_LOGIN = 100;
    public static final int STATUS_QUIT = 110;
    public static final int STATUS_OK = 250;

    public static final int PLUG_ON = 1;
    public static final int PLUG_OFF = 0;

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

    private final HashHelper hashHelper;

    private final SynchronousTelnetClient telnetClient;

    private final PlugConfig config;

    public NetioPlugClient(HashHelper hashHelper, SynchronousTelnetClient telnetClient, PlugConfig config) {
        this.hashHelper = hashHelper;
        this.telnetClient = telnetClient;
        this.config = config;
    }

    public void login() {
        telnetClient.connect();

        String welcomeMessage = stripCommandCode(waitForAcknowledge(STATUS_LOGIN));
        String hashValue = getWelcomeHashcode(welcomeMessage);

        sendAndWaitForAcknowledge(getLoginMessage(hashValue), STATUS_OK);
    }


    public void enablePlugPort() {
        sendAndWaitForAcknowledge(getPortEnableDisableMessage(PLUG_ON), STATUS_OK);
    }

    public void disablePlugPort() {
        sendAndWaitForAcknowledge(getPortEnableDisableMessage(PLUG_OFF), STATUS_OK);
    }

    public void enablePlugPortTemporarily() {
        String response = sendAndWaitForAcknowledge(getSystemTimeMessage(), STATUS_OK);

        DateTime timeToStart = getTimeToStart(getSystemTime(response));
        DateTime timeToEnd = getTimeToEnd(timeToStart);

        sendAndWaitForAcknowledge(getEnablePortForPeriodOfTimeMessage(timeToStart, timeToEnd), STATUS_OK);
        sendAndWaitForAcknowledge(getTimerEnableMessage(), STATUS_OK);
    }

    public void disconnect() {
        sendAndWaitForAcknowledge(getQuitMessage(), STATUS_QUIT);
        telnetClient.disconnect();
    }

    private String getWelcomeHashcode(String welcomeMessage) {
        return welcomeMessage.replaceAll("^HELLO ", "").replaceAll(" - KSHELL.+", "");
    }

    private String stripCommandCode(String dateText) {
        return dateText.replaceAll("^[\\S]*", "").trim();
    }

    private String sendAndWaitForAcknowledge(String message, int statusCode) {
        telnetClient.send(message);
        return waitForAcknowledge(statusCode);
    }

    private String waitForAcknowledge(int statusCode) {
        return telnetClient.waitForMessage(new WaitForStatusCodePredicate(statusCode), DEFAULT_TIMEOUT);
    }

    private DateTime getSystemTime(String timeMessageText) {
        String timeText = stripCommandCode(timeMessageText);
        return dateTimeFormatter.parseDateTime(timeText);
    }

    private DateTime getTimeToStart(DateTime systemTime) {
        // The plug does not allow a start time that less than one minute away
        // TODO use plug config second values        
        return systemTime.plus(new Period(0, 1, 1, 0));
    }

    protected DateTime getTimeToEnd(DateTime timeToStart) {
        // TODO use plug config second values
        return timeToStart.plus(new Period(0, 0, 30, 0));
    }

    private String getLoginMessage(String hashValue) {
        String passwordToBeEncrypted = config.getAdminAccount() + config.getAdminPassword() + hashValue;
        String encryptedPassword = hashHelper.hashString(passwordToBeEncrypted, "MD5");

        // clogin <name> <password>
        return String.format("clogin %s %s", config.getAdminAccount(), encryptedPassword);
    }

    private String getSystemTimeMessage() {
        // system time <YYYY/MM/DD,HH:MM:SS>
        return "system time";
    }

    private String getPortEnableDisableMessage(int newState) {
        // port <output> [0 | 1 | manual | int]
        return String.format("port %d %d", config.getPlug().getPlugNumber(), newState);
    }

    private String getEnablePortForPeriodOfTimeMessage(DateTime timeToStart, DateTime timeToEnd) {
        String timeToStartText = timeToStart.toString(dateTimeFormatter);
        String timeToEndText = timeToEnd.toString(dateTimeFormatter);

        // port timer <output> <time_format> [ <mode: once | daily | weekly> <on-time> <off-time>] <week_schedule>
        return String.format("port timer %d dt once %s %s 1111111", config.getPlug().getPlugNumber(), timeToStartText,
                timeToEndText);
    }

    private String getTimerEnableMessage() {
        // port setup <output> [ <output_name> <mod: manual|timer> <interrupt_delay> <PON_status> ]
        return String.format("port setup %d %s timer 0 0", config.getPlug().getPlugNumber(), config.getPlug().name());
    }

    public String getQuitMessage() {
        return "quit";
    }
}