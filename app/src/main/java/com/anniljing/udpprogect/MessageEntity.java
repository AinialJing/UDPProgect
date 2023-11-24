package com.anniljing.udpprogect;

public class MessageEntity {
    public static final int ROLE_HOST=1;
    public static final int ROLE_CLIENT=2;
    private int role;
    private String message;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
