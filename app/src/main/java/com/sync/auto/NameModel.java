package com.sync.auto;

public class NameModel {
    private String name;
    private int status;

    public NameModel(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }
}
