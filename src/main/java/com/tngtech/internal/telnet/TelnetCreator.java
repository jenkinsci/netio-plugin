package com.tngtech.internal.telnet;

import com.google.common.annotations.VisibleForTesting;
import com.tngtech.internal.wrappers.Scanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TelnetCreator {


    public Thread getThread(Runnable runnable) {
        return new Thread(runnable);
    }

    public Socket getSocket(String hostName, int port) {
        try {
            return doGetSocket(hostName, port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @VisibleForTesting
    protected Socket doGetSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);
    }

    public Scanner getSocketReader(Socket socket) {
        return new Scanner(getSocketInputStream(socket));
    }

    public InputStream getSocketInputStream(Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public PrintWriter getSocketWriter(Socket socket) {
        return new PrintWriter(getSocketOutputStream(socket), true);
    }

    public OutputStream getSocketOutputStream(Socket socket) {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}