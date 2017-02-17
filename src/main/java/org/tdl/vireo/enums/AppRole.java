package org.tdl.vireo.enums;

import edu.tamu.framework.model.IRole;

public enum AppRole implements IRole {

    // NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
    NONE(0),
    STUDENT(1),
    REVIEWER(2),
    MANAGER(3),
    ADMINISTRATOR(4);

    private int value;

    AppRole(int value) {
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
