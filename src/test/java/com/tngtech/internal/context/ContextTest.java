package com.tngtech.internal.context;

import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.plugclient.PlugClient;
import com.tngtech.internal.plugclient.PlugClientCreator;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ContextTest {
    @Test
    public void testGetPlugClientCreator() {
        // First, assert that a loaded bean is not null
        PlugClientCreator plugClientCreator = Context.getBean(PlugClientCreator.class);
        assertThat(plugClientCreator, is(not(nullValue())));

        // Then assert that properties are injected correctly
        PlugConfig config = new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1");
        PlugClient plugClient = plugClientCreator.withPlugConfig(config).createClient();
        assertThat(plugClient, is(not(nullValue())));
    }
}