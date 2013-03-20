package com.tngtech.internal.plugin;

import com.tngtech.internal.plug.Plug;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, JSONObject.class})
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
    public void testIsApplicable() {
        assertThat(descriptor.isApplicable(null), is(true));
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
        assertThat(descriptor.getDefaultDelaySeconds(), is("61"));
        assertThat(descriptor.getDelaySeconds(), is(61));
        assertThat(descriptor.getDefaultActivationDurationSeconds(), is("30"));
        assertThat(descriptor.getActivationDurationSeconds(), is(30));
    }

    @Test
    public void checkHostNameValidity() {
        assertValidationResult(descriptor.doCheckHostName(""), FormValidation.Kind.ERROR, "Please enter some text");
        assertValidationResult(descriptor.doCheckHostName("sho"), FormValidation.Kind.WARNING, "The text seems to be too short");
        assertValidationResult(descriptor.doCheckHostName("localhost"), FormValidation.Kind.OK, null);
    }

    @Test
    public void checkHostPortValidity() {
        assertValidationResult(descriptor.doCheckHostPort(""), FormValidation.Kind.ERROR, "Please enter a port number between 0 and 65535");
        assertValidationResult(descriptor.doCheckHostPort("sho"), FormValidation.Kind.ERROR, "Please enter a port number between 0 and 65535");
        assertValidationResult(descriptor.doCheckHostPort("1234"), FormValidation.Kind.OK, null);
        assertValidationResult(descriptor.doCheckHostPort("-1"), FormValidation.Kind.ERROR, "Please enter a port number between 0 and 65535");
        assertValidationResult(descriptor.doCheckHostPort("70000"), FormValidation.Kind.ERROR, "Please enter a port number between 0 and 65535");
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

    @Test
    public void checkDelaySecondsValidity() {
        assertValidationResult(descriptor.doCheckDelaySeconds(""), FormValidation.Kind.ERROR,
                "Please enter a delay (in seconds) between 60 and 1000; the plug cannot be programmed to use delays shorter than 1 minute");
        assertValidationResult(descriptor.doCheckDelaySeconds("sho"), FormValidation.Kind.ERROR,
                "Please enter a delay (in seconds) between 60 and 1000; the plug cannot be programmed to use delays shorter than 1 minute");
        assertValidationResult(descriptor.doCheckDelaySeconds("30"), FormValidation.Kind.ERROR,
                "Please enter a delay (in seconds) between 60 and 1000; the plug cannot be programmed to use delays shorter than 1 minute");
        assertValidationResult(descriptor.doCheckDelaySeconds("1500"), FormValidation.Kind.ERROR,
                "Please enter a delay (in seconds) between 60 and 1000; the plug cannot be programmed to use delays shorter than 1 minute");
        assertValidationResult(descriptor.doCheckDelaySeconds("61"), FormValidation.Kind.OK, null);
    }

    @Test
    public void checkActivationDurationSecondsValidity() {
        assertValidationResult(descriptor.doCheckActivationDurationSeconds(""), FormValidation.Kind.ERROR,
                "Please enter a duration (in seconds) between 10 and 1000");
        assertValidationResult(descriptor.doCheckActivationDurationSeconds("sho"), FormValidation.Kind.ERROR,
                "Please enter a duration (in seconds) between 10 and 1000");
        assertValidationResult(descriptor.doCheckActivationDurationSeconds("5"), FormValidation.Kind.ERROR,
                "Please enter a duration (in seconds) between 10 and 1000");
        assertValidationResult(descriptor.doCheckActivationDurationSeconds("1500"), FormValidation.Kind.ERROR,
                "Please enter a duration (in seconds) between 10 and 1000");
        assertValidationResult(descriptor.doCheckActivationDurationSeconds("30"), FormValidation.Kind.OK, null);
    }

    @Test
    public void testFillPlugNumberItems() {
        ListBoxModel model = descriptor.doFillPlugNumberItems();

        assertThat(model, is(not(nullValue())));
        assertOption(model, 0, Plug.PLUG1.name(), Plug.PLUG1.getPlugNumber().toString());
        assertOption(model, 1, Plug.PLUG2.name(), Plug.PLUG2.getPlugNumber().toString());
        assertOption(model, 2, Plug.PLUG3.name(), Plug.PLUG3.getPlugNumber().toString());
        assertOption(model, 3, Plug.PLUG4.name(), Plug.PLUG4.getPlugNumber().toString());
    }

    @Test
    public void testConfigure() throws Descriptor.FormException {
        StaplerRequest request = mock(StaplerRequest.class);
        JSONObject formData = PowerMockito.mock(JSONObject.class);

        when(formData.getString("hostName")).thenReturn("hostName");
        when(formData.getInt("hostPort")).thenReturn(80);
        when(formData.getString("adminAccount")).thenReturn("adminAccount");
        when(formData.getString("adminPassword")).thenReturn("adminPassword");
        when(formData.getInt("delaySeconds")).thenReturn(61);
        when(formData.getInt("activationDurationSeconds")).thenReturn(30);

        boolean response = descriptor.configure(request, formData);

        assertThat(descriptor.getHostName(), is("hostName"));
        assertThat(descriptor.getHostPort(), is(80));
        assertThat(descriptor.getAdminAccount(), is("adminAccount"));
        assertThat(descriptor.getAdminPassword(), is("adminPassword"));
        assertThat(descriptor.getDelaySeconds(), is(61));
        assertThat(descriptor.getActivationDurationSeconds(), is(30));

        verify(descriptor).save();
        assertThat(response, is(true));
    }

    private void assertValidationResult(FormValidation result, FormValidation.Kind expectedResult, String expectedMessage) {
        assertThat(result.kind, is(expectedResult));
        assertThat(result.getMessage(), is(expectedMessage));
    }

    private void assertOption(ListBoxModel model, int index, String value, String displayedText) {
        ListBoxModel.Option option = model.get(index);
        assertThat(option.name, is(displayedText));
        assertThat(option.value, is(value));
    }
}