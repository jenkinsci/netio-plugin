package com.tngtech.internal.telnet;

import com.google.common.base.Predicate;
import com.google.common.collect.Queues;
import com.tngtech.internal.telnet.notifications.NotificationHandler;

import java.util.Queue;
import java.util.logging.Logger;

public class SynchronousTelnetClient implements TelnetClient {
    public static final int TIME_TO_SLEEP = 200;

    private final Logger logger = Logger.getLogger("TelnetClient");

    private AsynchronousTelnetClient telnetClient;

    private Queue<String> messageQueue;

    public SynchronousTelnetClient(AsynchronousTelnetClient telnetClient) {
        this.telnetClient = telnetClient;
        this.messageQueue = Queues.newLinkedBlockingQueue();

        initNotificationHandler();
    }

    private void initNotificationHandler() {
        telnetClient.addNotificationHandler(new NotificationHandler() {
            public void getNotification(String message) {
                logger.info(String.format("Incoming message: %s", message));
                messageQueue.offer(message);
            }
        });
    }

    public void connect() {
        telnetClient.connect();
    }

    public void disconnect() {
        telnetClient.disconnect();
    }

    public void send(String message) {
        logger.info(String.format("Outgoing message: %s", message));
        telnetClient.send(message);
    }

    public String waitForMessage(Predicate<String> predicate, int maxWaitMillis) {
        int waitedFor = 0;
        while (waitedFor < maxWaitMillis) {
            String message = searchMessageQueueForMatchingMessage(predicate);
            if (message != null) {
                return message;
            }

            sleep(TIME_TO_SLEEP);
            waitedFor += TIME_TO_SLEEP;
        }

        throw new IllegalStateException("The expected response did not arrive");
    }

    private String searchMessageQueueForMatchingMessage(Predicate<String> predicate) {
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.poll();
            if (predicate.apply(message)) {
                return message;
            }
        }
        return null;
    }

    private void sleep(int timeToSleep) {
        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}