package org.tdl.vireo.enums;

public enum Role {
    // NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
    NONE(0),
    USER(1),
    MANAGER(2),
    ADMINISTRATOR(3);

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
