package com.tngtech.internal.plugclient;

import com.google.inject.Inject;
import com.tngtech.internal.helpers.HashHelper;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.TelnetClientCreator;

import static com.google.common.base.Preconditions.checkState;

public class PlugClientCreator {

    private PlugConfig plugConfig;

    private TelnetClientCreator telnetClientCreator;

    private HashHelper hashHelper;

    @Inject
    public PlugClientCreator(TelnetClientCreator telnetClientCreator, HashHelper hashHelper) {
        this.telnetClientCreator = telnetClientCreator;
        this.hashHelper = hashHelper;
    }

    public PlugClientCreator withPlugConfig(PlugConfig plugConfig) {
        this.plugConfig = plugConfig;
        return this;
    }

    public PlugClient createClient() {
        checkState(plugConfig != null, "No plug config has been set so far");
        return new NetioPlugClient(hashHelper, telnetClientCreator.getSynchronousTelnetClient(plugConfig), plugConfig);
    }
}