package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.HashHelper;
import com.tngtech.internal.plug.Plug;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;

public class NetioPlugMessages {

    public static final int STATUS_LOGIN = 100;
    public static final int STATUS_QUIT = 110;
    public static final int STATUS_OK = 250;

    public static final int PLUG_ON = 1;
    public static final int PLUG_OFF = 0;

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd,HH:mm:ss");

    private final HashHelper hashHelper;

    @Inject
    public NetioPlugMessages(HashHelper hashHelper) {
        this.hashHelper = hashHelper;
    }

    public String getLoginMessage(String adminAccount, String adminPassword, String welcomeMessageText) {
        // clogin <name> <password>
        return String.format("clogin %s %s", adminAccount, getEncryptedPassword(adminAccount, adminPassword, welcomeMessageText));
    }

    public String getSystemTimeMessage() {
        // system time <YYYY/MM/DD,HH:MM:SS>
        return "system time";
    }

    public String getTimerMessage(Plug plug) {
        return String.format("port timer %d dt", plug.getPlugNumber());
    }

    public String getPortEnableDisableMessage(Plug plug, int newState) {
        // port <output> [0 | 1 | manual | int]
        return String.format("port %d %d", plug.getPlugNumber(), newState);
    }

    public String getEnablePortForPeriodOfTimeMessage(Plug plug, DateTime timeToStart, DateTime timeToEnd) {
        String timeToStartText = timeToStart.toString(dateTimeFormatter);
        String timeToEndText = timeToEnd.toString(dateTimeFormatter);

        // port timer <output> <time_format> [ <mode: once | daily | weekly> <on-time> <off-time>] <week_schedule>
        return String.format("port timer %d dt once %s %s 1111111", plug.getPlugNumber(), timeToStartText,
                timeToEndText);
    }

    public String getTimerEnableMessage(Plug plug) {
        // port setup <output> [ <output_name> <mod: manual|timer> <interrupt_delay> <PON_status> ]
        return String.format("port setup %d %s timer 0 0", plug.getPlugNumber(), plug.name());
    }

    public String getQuitMessage() {
        return "quit";
    }

    private String getEncryptedPassword(String adminAccount, String adminPassword, String welcomeMessageText) {
        String welcomeHashcode = getWelcomeHashcode(welcomeMessageText);
        String passwordToBeEncrypted = adminAccount + adminPassword + welcomeHashcode;
        return hashHelper.hashString(passwordToBeEncrypted, "MD5");
    }

    private String getWelcomeHashcode(String welcomeMessageText) {
        String welcomeMessage = stripCommandCode(welcomeMessageText);
        return welcomeMessage.replaceAll("^HELLO ", "").replaceAll(" - KSHELL.+", "");
    }

    public DateTime getSystemTime(String timeMessageText) {
        String timeText = stripCommandCode(timeMessageText);
        return dateTimeFormatter.parseDateTime(timeText);
    }


    public boolean isTimerSet(String timerMessageText) {
        return timerMessageText.contains("timer");
    }

    public DateTime getStartTimeFromTimerMessage(String response) {

        String[] tokens = stripCommandCode(response).split(" ");
        String startTimeToken = tokens[1];
        return dateTimeFormatter.parseDateTime(startTimeToken);
    }

    public DateTime getEndTimeFromTimerMessage(String response) {
        String[] tokens = stripCommandCode(response).split(" ");
        String endTimeToken = tokens[2];
        return dateTimeFormatter.parseDateTime(endTimeToken);
    }

    private String stripCommandCode(String dateText) {
        return dateText.replaceAll("^[\\S]*", "").trim();
    }


}