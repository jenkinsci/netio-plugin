package com.tngtech.internal.plugclient;

import com.google.common.base.Predicate;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;

public class NetioPlugClient {

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

    public NetioPlugClient(PlugConfig config) {
        this.config = config;
        telnetClient = new SynchronousTelnetClient(config);
    }

    public void login() {
        telnetClient.connect();
        System.out.println(telnetClient.waitForMessage(new WaitForStatusCodePredicate(100), 1000));
        telnetClient.send(getLoginMessage());
        System.out.println(telnetClient.waitForMessage(new WaitForStatusCodePredicate(250), 1000));
    }

    public void disconnect() {
        telnetClient.disconnect();
    }

    private String getLoginMessage() {
        return String.format("login %s %s", config.getAdminAccount(), config.getAdminPassword());
    }
}