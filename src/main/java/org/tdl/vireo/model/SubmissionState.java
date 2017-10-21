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

	public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }
	
	SubmissionState(int value) {
        this.value = value;
    }
}
