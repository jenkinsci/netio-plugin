package com.tngtech.internal.context;

import com.tngtech.internal.plugclient.PlugClientCreator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ValueBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetBean() {
        ValueBuilder valueBuilder = Context.getBean(ValueBuilder.class);

        PlugClientCreator plugClientCreator = valueBuilder.getBean(PlugClientCreator.class);
        assertThat(plugClientCreator, is(not(nullValue())));
    }

    @Test
    public void getBeanShouldThrowAnErrorWhenUsingNew() {
        ValueBuilder valueBuilder = new ValueBuilder(mock(Context.class));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("This class must be instantiated using DI");

        valueBuilder.getBean(PlugClientCreator.class);
    }

    @Test
    public void getBeanShouldThrowAnErrorWhenUsingNewAndNullValues() {
        ValueBuilder valueBuilder = new ValueBuilder(null);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("This class must be instantiated using DI");

        valueBuilder.getBean(PlugClientCreator.class);
    }
}