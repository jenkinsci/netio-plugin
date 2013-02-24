package com.tngtech.internal.plugclient;

public interface PlugClient {
    void login();

    void enablePlugPort();
    void disablePlugPort();
    void enablePlugPortTemporarily();

    void disconnect();
}
