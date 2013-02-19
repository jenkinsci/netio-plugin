package com.tngtech.internal.telnet;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.notifications.NotificationHandler;
import com.tngtech.internal.wrappers.Scanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class AsynchronousTelnetClient implements Runnable, TelnetClient {
    public enum State {
        NOT_CONNECTED,
        CONNECTED,
        DISCONNECTED
    }

    private final TelnetCreator telnetCreator;

    private final PlugConfig config;

    private Socket socket;
    private Thread readerThread;

    private Scanner scanner;
    private PrintWriter writer;

    private State currentState = State.NOT_CONNECTED;

    private final Set<NotificationHandler> notificationHandlers;

    public AsynchronousTelnetClient(TelnetCreator telnetCreator, PlugConfig config) {
        this.telnetCreator = telnetCreator;
        this.config = config;
        notificationHandlers = Sets.newHashSet();
    }

    public void connect() {
        checkIfNotConnected();
        currentState = State.CONNECTED;

        doConnect();
    }

    private void doConnect() {
        socket = telnetCreator.getSocket(config.getHostName(), config.getHostPort());
        scanner = telnetCreator.getSocketReader(socket);
        writer = telnetCreator.getSocketWriter(socket);

        readerThread = telnetCreator.getThread(this);
        readerThread.start();
    }

    public void disconnect() {
        checkIfConnected();
        currentState = State.DISCONNECTED;

        doDisconnect();
    }


    private void doDisconnect() {
        try {
            writer.close();
            scanner.close();
            socket.close();
            readerThread.interrupt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void send(String text) {
        checkIfConnected();
        writer.println(text);
    }

    @VisibleForTesting
    public void run() {
        waitForResponseAndSendEvents();
    }

    private void waitForResponseAndSendEvents() {
        while (scanner.hasNextLine()) {
            sendNotification(scanner.nextLine());
        }
    }

    @VisibleForTesting
    protected void sendNotification(String message) {
        for (NotificationHandler notificationHandler : notificationHandlers) {
            notificationHandler.getNotification(message);
        }
    }

    public void addNotificationHandler(NotificationHandler handler) {
        notificationHandlers.add(handler);
    }

    public void removeNotificationHandler(NotificationHandler handler) {
        if (notificationHandlers.contains(handler)) {
            notificationHandlers.remove(handler);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    private void checkIfNotConnected() {
        checkState(currentState != State.CONNECTED, "The client is already connected");
        checkState(currentState != State.DISCONNECTED, "The client has already been disconnected");
    }

    private void checkIfConnected() {
        checkState(currentState != State.NOT_CONNECTED, "The client has not yet been connected");
        checkState(currentState != State.DISCONNECTED, "The client has already been disconnected");
    }
}