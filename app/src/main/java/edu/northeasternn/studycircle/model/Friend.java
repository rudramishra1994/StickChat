package edu.northeasternn.studycircle.model;

public class Friend implements  Comparable{
    private  String fullName;
    private  String Email;

    public Friend(String fullName, String email) {
        this.fullName = fullName;
        Email = email;
    }

    public Friend() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public int compareTo(Object o) {
        return this.fullName.compareTo(((Friend)o).getFullName());
    }
}