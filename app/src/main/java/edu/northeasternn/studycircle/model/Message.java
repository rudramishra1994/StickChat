package edu.northeasternn.studycircle.model;

public class Message implements Comparable{
    private String messageUser;
    private String messageUserId;
    private String messageText;
    private String groupId;
    private long messageTime;

    public Message(){

    }

    public Message(String messageUser, String messageUserId, String messageText, String groupId,long messageTime) {
        this.messageUser = messageUser;
        this.messageUserId = messageUserId;
        this.messageText = messageText;
        this.groupId = groupId;

        this.messageTime = messageTime;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public int compareTo(Object o) {
      if(this.getMessageTime()-((Message)o).getMessageTime()>0) return 1;
      else if(this.getMessageTime()-((Message)o).getMessageTime()<0) return -1;
      else return 0;
    }
}