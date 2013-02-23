package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;

public class NetioPlugClient implements PlugClient {

    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 0;

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

    public void disconnect() {
        telnetClient.disconnect();
    }

    private String getLoginMessage() {
        return String.format("login %s %s", config.getAdminAccount(), config.getAdminPassword());
    }

    private String getPortMessage(int newState) {
        return String.format("port %d %d", config.getPlug().getPlugNumber(), newState);
    }
}