package com.tngtech.internal.plug;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PlugTest {

    @Test
    public void testGetPlugNumber() {
        assertThat(Plug.PLUG1.getPlugNumber(), is(1));
        assertThat(Plug.PLUG2.getPlugNumber(), is(2));
        assertThat(Plug.PLUG3.getPlugNumber(), is(3));
        assertThat(Plug.PLUG4.getPlugNumber(), is(4));
    }

    @Test
    public void testToString() {
        assertThat(Plug.PLUG1.toString(), is("PLUG1"));
        assertThat(Plug.PLUG2.toString(), is("PLUG2"));
        assertThat(Plug.PLUG3.toString(), is("PLUG3"));
        assertThat(Plug.PLUG4.toString(), is("PLUG4"));
    }
}
