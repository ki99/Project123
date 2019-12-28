package com.example.practice.ui.main.First;

public class Dictionary {
    private String index;
    private String name;
    private String group;
    private String number;

    public Dictionary () {}

    public Dictionary(String index, String name, String group, String number) {
        this.index = index;
        this.name = name;
        this.group = group;
        this.number = number;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
