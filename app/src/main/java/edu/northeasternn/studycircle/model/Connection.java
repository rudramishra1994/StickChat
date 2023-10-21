package edu.northeasternn.studycircle.model;

import org.checkerframework.checker.units.qual.C;

import edu.northeasternn.studycircle.util.ConnectionStatus;

public class Connection implements Comparable {
    private String sender_FN;
    private String sender_email;
    private String receiver_FN;
    private String receiver_email;
    private String sendTime;

    private ConnectionStatus status;

    public Connection(String sender_FN, String sender_email, String receiver_FN, String receiver_email, String sendTime,ConnectionStatus status) {
        this.sender_FN = sender_FN;
        this.sender_email = sender_email;
        this.receiver_FN = receiver_FN;
        this.receiver_email = receiver_email;
        this.sendTime = sendTime;
        this.status = status;
    }

    public Connection(Connection obj){
        this.sender_FN = obj.getSender_FN();
        this.sender_email = obj.getSender_email();
        this.receiver_FN = obj.getReceiver_FN();
        this.receiver_email = obj.getReceiver_email();
        this.sendTime = obj.getSendTime();
        this.status = obj.getStatus();
    }

    public Connection() {
    }

    public String getSender_FN() {
        return sender_FN;
    }

    public String getSender_email() {
        return sender_email;
    }

    public String getReceiver_FN() {
        return receiver_FN;
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public String getSendTime() {
        return sendTime;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    @Override
    public int compareTo(Object o) {
        Long requestTime1 = Long.parseLong(this.sendTime);
        Connection o2 = (Connection) o;
        Long requestTime2 = Long.parseLong(o2.getSendTime());
        return requestTime1.compareTo(requestTime2);
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }


}