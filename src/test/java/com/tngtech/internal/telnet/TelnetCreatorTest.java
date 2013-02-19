package com.tngtech.internal.telnet;

import com.tngtech.internal.wrappers.Scanner;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TelnetCreatorTest {


    private static class BooleanValue {
        public boolean value = false;
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TelnetCreator telnetCreator;

    @Before
    public void setUp() {
        telnetCreator = spy(new TelnetCreator());
    }

    @Test
    public void testGetAsynchronousTelnetClient() {

    }

    @Test
    public void testGetThread() {
        final BooleanValue hasRun = new BooleanValue();
        Runnable runnable = new Runnable() {
            public void run() {
                hasRun.value = true;
            }
        };

        Thread thread = telnetCreator.getThread(runnable);
        thread.run();

        assertThat(hasRun.value, is(true));
    }

    @Test
    public void testGetSocket() {
        Socket socket = telnetCreator.getSocket("localhost", 80);

        assertThat(socket, is(not(nullValue())));
        assertThat(socket.getLocalAddress().getHostName(), is("localhost"));
        assertThat(socket.getPort(), is(80));
    }

    @Test
    public void whenGettingASocketIOExceptionsShouldBeRethrown() throws IOException {
        doThrow(IOException.class).when(telnetCreator).doGetSocket(anyString(), anyInt());
        expectedException.expect(IllegalStateException.class);

        telnetCreator.getSocket("localhost", 80);
    }

    @Test
    public void testGetSocketReader() throws IOException {
        Socket socket = mock(Socket.class);
        String myString = "line one\nline two";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(myString.getBytes());
        when(socket.getInputStream()).thenReturn(inputStream);

        Scanner scanner = telnetCreator.getSocketReader(socket);

        assertThat(scanner, is(not(nullValue())));
        assertTrue(scanner.hasNextLine());
        assertThat(scanner.nextLine(), is("line one"));

        assertTrue(scanner.hasNextLine());
        assertThat(scanner.nextLine(), is("line two"));
    }

    @Test
    public void testGetSocketInputStream() throws IOException {
        Socket socket = mock(Socket.class);
        InputStream expectedInputStream = mock(InputStream.class);

        when(socket.getInputStream()).thenReturn(expectedInputStream);

        InputStream returnedInputStream = telnetCreator.getSocketInputStream(socket);
        assertThat(returnedInputStream, is(sameInstance(expectedInputStream)));
    }

    @Test
    public void whenGettingSocketInputStreamAnErrorShouldBeRethrown() throws IOException {
        Socket socket = mock(Socket.class);
        doThrow(IOException.class).when(socket).getInputStream();

        expectedException.expect(IllegalStateException.class);

        telnetCreator.getSocketInputStream(socket);
    }

    @Test
    public void testGetSocketWriter() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);

        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);

        PrintWriter writer = telnetCreator.getSocketWriter(socket);
        writer.println("line");

        verify(outputStream).write(argThat(new BaseMatcher<byte[]>() {

            public boolean matches(Object object) {
                byte[] bytes = (byte[]) object;
                return (bytes[0] == 108 && bytes[1] == 105 && bytes[2] == 110 && bytes[3] == 101);
            }

            public void describeTo(Description description) {
                description.appendText("The bytes captured did not match");
            }
        }), eq(0), eq(5));
    }

    @Test
    public void testGetSocketOutputStream() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream expectedOutputStream = mock(OutputStream.class);

        when(socket.getOutputStream()).thenReturn(expectedOutputStream);

        OutputStream returnedOutputStream = telnetCreator.getSocketOutputStream(socket);
        assertThat(returnedOutputStream, is(sameInstance(expectedOutputStream)));
    }

    @Test
    public void whenGettingSocketOutputStreamAnErrorShouldBeRethrown() throws IOException {
        Socket socket = mock(Socket.class);
        doThrow(IOException.class).when(socket).getOutputStream();

        expectedException.expect(IllegalStateException.class);

        telnetCreator.getSocketOutputStream(socket);
    }
}