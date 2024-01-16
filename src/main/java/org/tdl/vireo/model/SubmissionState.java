package org.tdl.vireo.model;

public enum SubmissionState {
    NONE(0),
    IN_PROGRESS(1),
    SUBMITTED(2),
    UNDER_REVIEW(3),
    NEEDS_CORRECTIONS(4),
    CORRECTIONS_RECIEVED(5),
    WAITING_ON_REQUIREMENTS(6),
    APPROVED(7),
    PENDING_PUBLICATION(8),
    PUBLISHED(9),
    ON_HOLD(10),
    WITHDRAWN(11),
    CANCELED(12);

    private int value;

    SubmissionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static SubmissionState from(int value) {
        switch (value) {
            case 0:  return SubmissionState.NONE;
            case 1:  return SubmissionState.IN_PROGRESS;
            case 2:  return SubmissionState.SUBMITTED;
            case 3:  return SubmissionState.UNDER_REVIEW;
            case 4:  return SubmissionState.NEEDS_CORRECTIONS;
            case 5:  return SubmissionState.CORRECTIONS_RECIEVED;
            case 6:  return SubmissionState.WAITING_ON_REQUIREMENTS;
            case 7:  return SubmissionState.APPROVED;
            case 8:  return SubmissionState.PENDING_PUBLICATION;
            case 9:  return SubmissionState.PUBLISHED;
            case 10: return SubmissionState.ON_HOLD;
            case 11: return SubmissionState.WITHDRAWN;
            case 12: return SubmissionState.CANCELED;
            default:
                return SubmissionState.NONE;
        }
    }

}
