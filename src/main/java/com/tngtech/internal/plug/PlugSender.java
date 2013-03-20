package com.tngtech.internal.plug;

import com.tngtech.internal.context.Context;
import com.tngtech.internal.plugclient.PlugClient;
import com.tngtech.internal.plugclient.PlugClientCreator;
import hudson.model.BuildListener;

public class PlugSender {

    public void send(BuildListener listener, PlugConfig config) {
        logConfig(listener, config);
        enablePlugPortTemporarily(config);
    }

    private void logConfig(BuildListener listener, PlugConfig config) {
        String output = String.format("Using connection to %s:%s@%s:%d for plug number %s, delaying %d seconds, then activating for %d seconds",
                config.getAdminAccount(), config.getAdminPassword(), config.getHostName(),
                config.getHostPort(), config.getPlug().getPlugNumber(),
                config.getDelaySeconds(), config.getActivationDurationSeconds());
        listener.getLogger().println(output);
    }

    private void enablePlugPortTemporarily(PlugConfig config) {
        PlugClient plugClient = getPlugClient(config);
        plugClient.login();
        if (plugClient.shouldEnable()) {
            plugClient.enablePlugPortTemporarily();
        }
        plugClient.disconnect();
    }

    public PlugClient getPlugClient(PlugConfig config) {
        PlugClientCreator plugClientCreator = Context.getBean(PlugClientCreator.class);
        return plugClientCreator.withPlugConfig(config).createClient();
    }
}