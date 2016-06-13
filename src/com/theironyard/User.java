package com.theironyard;

import java.util.ArrayList;

public class User {
    String name;
    String password;
    ArrayList<Message> messages;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.messages = new ArrayList<>();
    }
}