package com.tngtech.internal.plug;

public class PlugConfig {
    private final String hostName;
    private final int hostPort;

    private final String adminAccount;
    private final String adminPassword;

    private Plug plug;

    public PlugConfig(String hostName, int hostPort, String adminAccount, String adminPassword, String plugNumberId) {
        this.hostName = hostName;
        this.hostPort = hostPort;

        this.adminAccount = adminAccount;
        this.adminPassword = adminPassword;

        this.plug = Plug.valueOf(plugNumberId);
    }

    public String getHostName() {
        return hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public Plug getPlug() {
        return plug;
    }
}
