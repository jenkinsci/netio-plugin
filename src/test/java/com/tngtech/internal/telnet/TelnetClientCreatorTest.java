package com.tngtech.internal.telnet;

import com.tngtech.internal.plug.PlugConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TelnetClientCreator.class)
public class TelnetClientCreatorTest {

    @Mock
    private TelnetCreator telnetCreator;

    @Mock
    private AsynchronousTelnetClient asynchronousTelnetClient;

    @Mock
    private SynchronousTelnetClient synchronousTelnetClient;

    @Mock
    private PlugConfig plugConfig;

    private TelnetClientCreator telnetClientCreator;

    @Before
    public void setUp() {
        telnetClientCreator = spy(new TelnetClientCreator());
    }

    @Test
    public void testGetTelnetCreator() {
        assertThat(telnetClientCreator.getTelnetCreator(), is(not(nullValue())));
    }

    @Test
    public void testGetAsynchronousTelnetClient() throws Exception {
        doReturn(telnetCreator).when(telnetClientCreator).getTelnetCreator();
        whenNew(AsynchronousTelnetClient.class).withAnyArguments().thenReturn(asynchronousTelnetClient);

        assertThat(telnetClientCreator.getAsynchronousTelnetClient(plugConfig), is(not(nullValue())));
        verifyNew(AsynchronousTelnetClient.class).withArguments(telnetCreator, plugConfig);
    }

    @Test
    public void testGetSynchronousTelnetClient() throws Exception {
        doReturn(telnetCreator).when(telnetClientCreator).getTelnetCreator();
        whenNew(AsynchronousTelnetClient.class).withAnyArguments().thenReturn(asynchronousTelnetClient);
        whenNew(SynchronousTelnetClient.class).withAnyArguments().thenReturn(synchronousTelnetClient);

        assertThat(telnetClientCreator.getSynchronousTelnetClient(plugConfig), is(not(nullValue())));
        verifyNew(SynchronousTelnetClient.class).withArguments(asynchronousTelnetClient);
    }
}