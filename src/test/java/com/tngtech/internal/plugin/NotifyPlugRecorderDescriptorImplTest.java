package com.tngtech.internal.plugin;

import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class NotifyPlugRecorderDescriptorImplTest {
    @Mock
    private Jenkins jenkins;

    private NotifyPlugRecorder.DescriptorImpl descriptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);
        when(Jenkins.getInstance()).thenReturn(jenkins);
        when(jenkins.getRootDir()).thenReturn(new File("/somewhere/no/one/has/ever/gone/before"));

        descriptor = spy(new NotifyPlugRecorder.DescriptorImpl());
        doNothing().when(descriptor).save();
    }

    @Test
    public void testDisplayName() {
        assertThat(descriptor.getDisplayName(), is("Notify the plug"));
    }

    @Test
    public void testGetDefaultInitialValues() {
        assertThat(descriptor.getDefaultHostName(), is("net.io"));
        assertThat(descriptor.getHostName(), is("net.io"));
        assertThat(descriptor.getDefaultHostPort(), is("1234"));
        assertThat(descriptor.getHostPort(), is(1234));
        assertThat(descriptor.getDefaultAdminAccount(), is("admin"));
        assertThat(descriptor.getAdminAccount(), is("admin"));
        assertThat(descriptor.getDefaultAdminPassword(), is("admin"));
        assertThat(descriptor.getAdminPassword(), is("admin"));
    }

    @Test
    public void checkHostNameValidity() {
        assertValidationResult(descriptor.doCheckHostName(""), FormValidation.Kind.ERROR, "Please enter some text");
        assertValidationResult(descriptor.doCheckHostName("sho"), FormValidation.Kind.WARNING, "The text seems to be too short");
        assertValidationResult(descriptor.doCheckHostName("localhost"), FormValidation.Kind.OK, null);
    }

    @Test
    public void checkHostPortValidity() {
        assertValidationResult(descriptor.doCheckHostPort(""), FormValidation.Kind.ERROR, "Please enter a number");
        assertValidationResult(descriptor.doCheckHostPort("sho"), FormValidation.Kind.ERROR, "Please enter a number");
        assertValidationResult(descriptor.doCheckHostPort("1234"), FormValidation.Kind.OK, null);
    }

    @Test
    public void checkAdminAccountValidity() {
        assertValidationResult(descriptor.doCheckAdminAccount(""), FormValidation.Kind.ERROR, "Please enter some text");
        assertValidationResult(descriptor.doCheckAdminAccount("adm"), FormValidation.Kind.WARNING, "The text seems to be too short");
        assertValidationResult(descriptor.doCheckAdminAccount("admin"), FormValidation.Kind.OK, null);
    }

    @Test
    public void checkAdminPasswordValidity() {
        assertValidationResult(descriptor.doCheckAdminPassword(""), FormValidation.Kind.ERROR, "Please enter some text");
        assertValidationResult(descriptor.doCheckAdminPassword("pas"), FormValidation.Kind.WARNING, "The text seems to be too short");
        assertValidationResult(descriptor.doCheckAdminPassword("password"), FormValidation.Kind.OK, null);
    }


    private void assertValidationResult(FormValidation result, FormValidation.Kind expectedResult, String expectedMessage) {
        assertThat(result.kind, is(expectedResult));
        assertThat(result.getMessage(), is(expectedMessage));
    }

}