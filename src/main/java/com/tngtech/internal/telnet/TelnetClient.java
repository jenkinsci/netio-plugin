package com.tngtech.internal.telnet;

public interface TelnetClient {
    void connect();

    void disconnect();

    void send(String text);
}