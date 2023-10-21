package edu.northeasternn.studycircle.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Sample {

    String Username;
    List<Connection> sent;
    List<Connection> received;

    public Sample(String username, List<Connection> sent, List<Connection> received) {
        Username = username;
        this.sent = sent;
        this.received = received;
    }

    public Sample() {
    }

    public List<Connection> getSent() {
        return sent;
    }

    public void setSent(List<Connection> sent) {
        this.sent = sent;
    }

    public List<Connection> getReceived() {
        return received;
    }

    public void setReceived(List<Connection> received) {
        this.received = received;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    @NonNull
    @Override
    public String toString() {
        return getUsername()+sent.toString()+received.toString();
    }
}