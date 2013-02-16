package com.tngtech.internal.telnet;

import com.google.common.collect.Sets;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.telnet.notifications.NotificationHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class AsynchronousTelnetClient implements Runnable, TelnetClient {

    private final TelnetCreator telnetCreator;

    private final PlugConfig config;

    private Socket socket;
    private Thread readerThread;

    private Scanner scanner;
    private PrintWriter writer;

    private final Set<NotificationHandler> notificationHandlers;

    public AsynchronousTelnetClient(TelnetCreator telnetCreator, PlugConfig config) {
        this.telnetCreator = telnetCreator;
        this.config = config;
        notificationHandlers = Sets.newHashSet();
    }

    public void connect() {
        socket = telnetCreator.getSocket(config.getHostName(), config.getHostPort());
        scanner = telnetCreator.getSocketScanner(socket);
        writer = telnetCreator.getSocketWriter(socket);

        readerThread = telnetCreator.getThread(this);
        readerThread.start();
    }

    public void disconnect() {
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
        writer.println(text);
    }

    public void run() {
        waitForResponseAndSendEvents();
    }

    private void waitForResponseAndSendEvents() {
        while (scanner.hasNextLine()) {
            sendNotification(scanner.nextLine());
        }
    }

    private void sendNotification(String message) {
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
}