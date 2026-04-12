package com.kofta.app.registry;

import javax.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlertRegistry {
    private final Map<String, Session> activeSenders = new ConcurrentHashMap<>();
    private final Map<String, String> senderEmails = new ConcurrentHashMap<>();
    private final Map<String, ReceiverConfig> activeReceivers = new ConcurrentHashMap<>();

    public record ReceiverConfig(String receiverEmail, String senderRef) {}

    public void addSender(String name, String email, Session session) {
        activeSenders.put(name, session);
        senderEmails.put(name, email);
    }

    public void removeSender(String name) {
        activeSenders.remove(name);
        senderEmails.remove(name);
    }

    public void addReceiver(String name, String receiverEmail, String senderRef) {
        activeReceivers.put(name, new ReceiverConfig(receiverEmail, senderRef));
    }

    public void removeReceiver(String name) {
        activeReceivers.remove(name);
    }

    public Session getSessionForSender(String senderRef) {
        return activeSenders.get(senderRef);
    }

    public String getEmailForSender(String senderRef) {
        return senderEmails.get(senderRef);
    }

    public List<ReceiverConfig> getAllReceivers() {
        return new ArrayList<>(activeReceivers.values());
    }

    public boolean hasSender(String senderRef) {
        return activeSenders.containsKey(senderRef);
    }
}