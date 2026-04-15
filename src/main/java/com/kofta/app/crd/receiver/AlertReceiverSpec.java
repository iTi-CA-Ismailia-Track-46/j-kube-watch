package com.kofta.app.crd.receiver;

public final class AlertReceiverSpec {
    private String email;
    private String senderRef;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenderRef() {
        return senderRef;
    }

    public void setSenderRef(String senderRef) {
        this.senderRef = senderRef;
    }
}
