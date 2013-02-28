package com.tngtech.internal.plugclient;

import com.tngtech.internal.helpers.HashHelper;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.SynchronousTelnetClient;
import com.tngtech.internal.telnet.TelnetClientCreator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlugClientCreator.class)
public class PlugClientCreatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private NetioPlugClient netioPlugClient;

    @Mock
    private PlugConfig plugConfig;

    @Mock
    private TelnetClientCreator telnetClientCreator;

    @Mock
    private HashHelper hashHelper;

    @Mock
    private SynchronousTelnetClient telnetClient;

    private PlugClientCreator plugClientCreator;

    @Before
    public void setUp() throws Exception {
        plugClientCreator = new PlugClientCreator(telnetClientCreator, hashHelper);
        when(telnetClientCreator.getSynchronousTelnetClient(any(PlugConfig.class))).thenReturn(telnetClient);

        whenNew(NetioPlugClient.class).withAnyArguments().thenReturn(netioPlugClient);
    }

    @Test
    public void testGetPlugClient() throws Exception {
        PlugClient plugClient = plugClientCreator.withPlugConfig(plugConfig).createClient();

        assertThat(plugClient, is(sameInstance((PlugClient) netioPlugClient)));
        verify(telnetClientCreator).getSynchronousTelnetClient(plugConfig);
        verifyNew(NetioPlugClient.class).withArguments(hashHelper, telnetClient, plugConfig);
    }

    @Test
    public void getPlugClientShouldThrowAnExceptionWhenNoPlugConfigIsGiven() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("No plug config has been set so far");

        plugClientCreator.createClient();
    }
}