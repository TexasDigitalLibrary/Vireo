package org.tdl.vireo.enums;

public enum EmbargoGuarantor {
    // NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
    DEFAULT(0),
    PROQUEST(1);

    private int value;

    EmbargoGuarantor(int num) {
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
