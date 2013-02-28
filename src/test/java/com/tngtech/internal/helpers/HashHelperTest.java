package com.tngtech.internal.helpers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HashHelperTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HashHelper hashHelper;

    @Before
    public void setUp() {
        hashHelper = new HashHelper();
    }

    @Test
    public void testHashString() {
        assertThat(hashHelper.hashString("test", "MD5"), is("098f6bcd4621d373cade4e832627b4f6"));
    }

    @Test
    public void whenTryingToHashWithAnUnknownAlgorithmTheExceptionShouldBeRethrown() {
        expectedException.expect(IllegalStateException.class);

        hashHelper.hashString("hash", "unknown");
    }
}
