package com.tngtech.internal.plugin;

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

        descriptor = new NotifyPlugRecorder.DescriptorImpl();
    }

    @Test
    public void testDisplayName() {
        assertThat(descriptor.getDisplayName(), is("Notify the plug"));
    }
}
