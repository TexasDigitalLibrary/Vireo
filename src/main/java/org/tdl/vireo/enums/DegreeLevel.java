package org.tdl.vireo.enums;


/**
 * The possible degree levels supported by vireo.
 *
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum DegreeLevel {
    NONE(0),
    UNDERGRADUATE(1),
    MASTERS(2),
    DOCTORAL(3);

    // The value for this degree level.
    private int value;

    /**
     * Private constructor for the defined degree levels listed above.
     *
     * @param id
     *            The id of the degree level.
     */
    private DegreeLevel(int value) {
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
