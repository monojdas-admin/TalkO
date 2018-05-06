package com.manojdas.admin.talko.misc;

/**
 * Created by Manoj Das on 03-Apr-18.
 */

public class Contact {
    String name;
    String number;
    String id;

    public Contact() {
    }

    public Contact(String name, String number, String id) {
        this.name = name;
        this.number = number;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
