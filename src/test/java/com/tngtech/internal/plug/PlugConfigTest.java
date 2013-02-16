package com.tngtech.internal.plug;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PlugConfigTest {

    @Test
    public void testConfig() {
        PlugConfig config = new PlugConfig("hostName", 80, "adminAccount", "adminPassword", "PLUG1");

        assertThat(config.getHostName(), is("hostName"));
        assertThat(config.getHostPort(), is(80));
        assertThat(config.getAdminAccount(), is("adminAccount"));
        assertThat(config.getAdminPassword(), is("adminPassword"));
        assertThat(config.getPlug(), is(Plug.PLUG1));
    }
}