package com.tngtech.internal.plugin;

import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.plug.PlugSender;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({NotifyPlugRecorder.class, Jenkins.class, PlugConfig.class, PlugSender.class})
public class NotifyPlugRecorderTest {
    @Mock
    private Jenkins jenkins;

    @Mock
    private NotifyPlugRecorder.DescriptorImpl descriptor;

    @Mock
    private AbstractBuild build;

    @Mock
    private PlugConfig plugConfig;

    @Mock
    private PlugSender plugSender;

    private NotifyPlugRecorder notifyPlugRecorder;

    @Before
    public void setUp() throws Exception {
        notifyPlugRecorder = new NotifyPlugRecorder("PLUG1");

        PowerMockito.mockStatic(Jenkins.class);
        when(Jenkins.getInstance()).thenReturn(jenkins);
        when(jenkins.getDescriptorOrDie((Class) anyObject())).thenReturn(descriptor);

        whenNew(PlugConfig.class).withAnyArguments().thenReturn(plugConfig);
        whenNew(PlugSender.class).withAnyArguments().thenReturn(plugSender);
    }

    @Test
    public void testPlugNumber() {
        assertThat(notifyPlugRecorder.getPlugNumber(), is("PLUG1"));
    }

    @Test
    public void testGetRequiredMonitorService() {
        assertThat(notifyPlugRecorder.getRequiredMonitorService(), is(BuildStepMonitor.NONE));
    }

    @Test
    public void testGetDescriptor() {
        assertThat(notifyPlugRecorder.getDescriptor(), is(descriptor));
        verify(jenkins).getDescriptorOrDie(NotifyPlugRecorder.class);
    }

    @Test
    public void whenResultIsSuccessNothingShouldBeDoneWhenPerforming() throws IOException, InterruptedException {
        when(build.getResult()).thenReturn(Result.SUCCESS);

        boolean result = notifyPlugRecorder.perform(build, null, null);

        assertThat(result, is(true));
        verify(plugSender, never()).send(any(BuildListener.class), any(PlugConfig.class));
    }

    @Test
    public void whenResultIsNoSuccessTheSenderShouldBeInvoked() throws Exception {
        when(build.getResult()).thenReturn(Result.FAILURE);

        when(descriptor.getHostName()).thenReturn("hostName");
        when(descriptor.getHostPort()).thenReturn(80);
        when(descriptor.getAdminAccount()).thenReturn("adminAccount");
        when(descriptor.getAdminPassword()).thenReturn("adminPassword");
        when(descriptor.getDelaySeconds()).thenReturn(61);
        when(descriptor.getActivationDurationSeconds()).thenReturn(30);

        boolean result = notifyPlugRecorder.perform(build, null, null);

        assertThat(result, is(true));
        verifyNew(PlugConfig.class).withArguments("hostName", 80, "adminAccount", "adminPassword", "PLUG1", 61, 30);
        verify(plugSender, times(1)).send(any(BuildListener.class), eq(plugConfig));
    }
}