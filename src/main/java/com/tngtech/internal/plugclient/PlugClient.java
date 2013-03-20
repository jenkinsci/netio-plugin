package com.tngtech.internal.plugclient;

public interface PlugClient {
    void login();

    void enablePlugPort();
    void disablePlugPort();
    boolean shouldEnable();
    void enablePlugPortTemporarily();

    void disconnect();
}
