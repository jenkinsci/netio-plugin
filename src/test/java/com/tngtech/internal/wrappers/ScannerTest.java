package com.tngtech.internal.wrappers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Scanner.class)
public class ScannerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private java.util.Scanner internalScanner;

    private Scanner scanner;

    @Before
    public void setUp() {
        scanner = new Scanner(internalScanner);
    }

    @Test
    public void testHasNextLine() {
        when(internalScanner.hasNextLine()).thenReturn(false);
        assertThat(scanner.hasNextLine(), is(false));

        when(internalScanner.hasNextLine()).thenReturn(true);
        assertThat(scanner.hasNextLine(), is(true));
    }

    @Test
    public void testNextLine() {
        when(internalScanner.nextLine()).thenReturn("text");
        assertThat(scanner.nextLine(), is("text"));
    }

    @Test
    public void testClose() {
        doThrow(new IllegalStateException()).when(internalScanner).close();
        expectedException.expect(IllegalStateException.class);

        scanner.close();
    }
}
