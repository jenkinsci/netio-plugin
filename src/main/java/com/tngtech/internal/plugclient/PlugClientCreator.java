package com.tngtech.internal.plugclient;

import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.TelnetClientCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkState;

@Component
public class PlugClientCreator {

    private PlugConfig plugConfig;

    private TelnetClientCreator telnetClientCreator;

    @Autowired
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