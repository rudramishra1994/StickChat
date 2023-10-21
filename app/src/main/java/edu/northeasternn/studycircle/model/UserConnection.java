package edu.northeasternn.studycircle.model;

import java.util.List;
import java.util.Set;

public class UserConnection {

    String userName;
    List<String> connectionList;

    public UserConnection(String userName, List<String> connectionList) {
        this.userName = userName;
        this.connectionList = connectionList;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<String> connectionList) {
        this.connectionList = connectionList;
    }
}