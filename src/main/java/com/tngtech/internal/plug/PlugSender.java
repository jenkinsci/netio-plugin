package com.tngtech.internal.plug;

import hudson.model.BuildListener;

public class PlugSender {

    public void send(BuildListener listener, PlugConfig config) {

        String output = String.format("Using connection to %s:%s@%s:%d for plug number %s",
                config.getAdminAccount(), config.getAdminPassword(), config.getHostName(),
                config.getHostPort(), config.getPlug().getPlugNumber());
        listener.getLogger().println(output);
    }

}
