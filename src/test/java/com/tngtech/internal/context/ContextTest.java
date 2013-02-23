package com.tngtech.internal.context;

import com.tngtech.internal.plugclient.PlugClientCreator;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ContextTest {
    @Test
    public void testGetPlugClientCreator() {
        PlugClientCreator plugClientCreator = Context.getBean(PlugClientCreator.class);
        assertThat(plugClientCreator, is(not(nullValue())));
    }
}
