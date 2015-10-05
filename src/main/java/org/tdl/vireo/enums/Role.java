package org.tdl.vireo.enums;

public enum Role {
    // NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
    NONE(0),
    USER(1),
    ADMINISTRATOR(2);

    private int value;

    Role(int num) {
        value = num;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
