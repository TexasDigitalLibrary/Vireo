package org.tdl.vireo.enums;

public enum EmbargoGuarantor {

    // NEVER CHANGE THE VALUES OR YOU'LL RUIN THE DB
    DEFAULT(0),
    PROQUEST(1);

    private int value;

    EmbargoGuarantor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Takes a string, makes it uppercase and sees if we have a matching enum value for it.
     *
     * @param from
     * @return
     */
    public static EmbargoGuarantor fromString(String from) {
        if (from != null) {
            for (EmbargoGuarantor val : EmbargoGuarantor.values()) {
                if (val.toString().equals(from.toUpperCase())) {
                    return val;
                }
            }
        }
        return null;
    }
}
