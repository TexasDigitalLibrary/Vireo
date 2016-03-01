package org.tdl.vireo.enums;

public enum Role {
    // NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
    NONE(0),
    STUDENT(1),
    REVIEWER(2),
    MANAGER(3),
    ADMINISTRATOR(4);

    private int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
