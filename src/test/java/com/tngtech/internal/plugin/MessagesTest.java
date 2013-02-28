package com.tngtech.internal.plugin;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MessagesTest {

    @Test
    public void testText_description() {
        assertThat(Messages.text_description(), is("Notify the plug"));
        assertThat(Messages._text_description().toString(), is("Notify the plug"));
    }

    @Test
    public void testDefaults_host_name() {
        assertThat(Messages.defaults_host_name(), is("net.io"));
        assertThat(Messages._defaults_host_name().toString(), is("net.io"));
    }

    @Test
    public void testDefaults_host_port() {
        assertThat(Messages.defaults_host_port(), is("1234"));
        assertThat(Messages._defaults_host_port().toString(), is("1234"));
    }

    @Test
    public void testDefaults_admin_account() {
        assertThat(Messages.defaults_admin_account(), is("admin"));
        assertThat(Messages._defaults_admin_account().toString(), is("admin"));
    }

    @Test
    public void testDefaults_admin_password() {
        assertThat(Messages.defaults_admin_password(), is("admin"));
        assertThat(Messages._defaults_admin_password().toString(), is("admin"));
    }

    @Test
    public void testWarning_tooShort() {
        assertThat(Messages.warning_tooShort(), is("The text seems to be too short"));
        assertThat(Messages._warning_tooShort().toString(), is("The text seems to be too short"));
    }

    @Test
    public void testError_notEntered() {
        assertThat(Messages.error_notEntered(), is("Please enter some text"));
        assertThat(Messages._error_notEntered().toString(), is("Please enter some text"));
    }

    @Test
    public void testError_noNumber() {
        assertThat(Messages.error_noNumber(), is("Please enter a number"));
        assertThat(Messages._error_noNumber().toString(), is("Please enter a number"));
    }
}