package com.tngtech.internal.plugin;

import hudson.tasks.BuildStepMonitor;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({NotifyPlugRecorder.class, Jenkins.class})
public class NotifyPlugRecorderTest {
    private NotifyPlugRecorder notifyPlugRecorder;

    @Mock
    private Jenkins jenkins;

    @Before
    public void setUp() {
        notifyPlugRecorder = new NotifyPlugRecorder("PLUG1");
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
        NotifyPlugRecorder.DescriptorImpl descriptor = mock(NotifyPlugRecorder.DescriptorImpl.class);

        PowerMockito.mockStatic(Jenkins.class);
        when(Jenkins.getInstance()).thenReturn(jenkins);
        when(jenkins.getDescriptorOrDie((Class) anyObject())).thenReturn(descriptor);

        assertThat(notifyPlugRecorder.getDescriptor(), is(descriptor));
        verify(jenkins).getDescriptorOrDie(NotifyPlugRecorder.class);
    }
}