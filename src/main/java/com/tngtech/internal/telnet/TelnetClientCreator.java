package com.tngtech.internal.telnet;

import com.google.common.annotations.VisibleForTesting;
import com.tngtech.internal.plug.PlugConfig;

public class TelnetClientCreator {
    public AsynchronousTelnetClient getAsynchronousTelnetClient(PlugConfig config) {
        return new AsynchronousTelnetClient(getTelnetCreator(), config);
    }

    public SynchronousTelnetClient getSynchronousTelnetClient(PlugConfig config) {
        return new SynchronousTelnetClient(getAsynchronousTelnetClient(config));
    }

    @VisibleForTesting
    protected TelnetCreator getTelnetCreator() {
        return new TelnetCreator();
    }
}