package com.tngtech.internal.plugclient;

import com.google.inject.Inject;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.TelnetClientCreator;

import static com.google.common.base.Preconditions.checkState;

public class PlugClientCreator {

    private PlugConfig plugConfig;

    private TelnetClientCreator telnetClientCreator;

    @Inject
    public PlugClientCreator(TelnetClientCreator telnetClientCreator) {
        this.telnetClientCreator = telnetClientCreator;
    }

    public PlugClientCreator withPlugConfig(PlugConfig plugConfig) {
        this.plugConfig = plugConfig;
        return this;
    }

    public PlugClient createClient() {
        checkState(plugConfig != null, "No plug config has been set so far");
        return new NetioPlugClient(telnetClientCreator.getSynchronousTelnetClient(plugConfig), plugConfig);
    }
}