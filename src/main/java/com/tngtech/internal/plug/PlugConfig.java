package com.tngtech.internal.plug;

public class PlugConfig {
    private final String hostName;
    private final int hostPort;

    private final String adminAccount;
    private final String adminPassword;

    private final Plug plug;

    private final int delaySeconds;
    private final int activationDurationSeconds;

    public PlugConfig(String hostName, int hostPort, String adminAccount, String adminPassword, String plugNumberId, int delaySeconds, int activationDurationSeconds) {
        this.hostName = hostName;
        this.hostPort = hostPort;

        this.adminAccount = adminAccount;
        this.adminPassword = adminPassword;

        this.plug = Plug.valueOf(plugNumberId);

        this.delaySeconds = delaySeconds;
        this.activationDurationSeconds = activationDurationSeconds;
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

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public int getActivationDurationSeconds() {
        return activationDurationSeconds;
    }
}
