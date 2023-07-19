package org.tdl.vireo.exception;

public class SwordDepositNotFoundException extends SwordDepositException {

    private static final long serialVersionUID = 7808340329570918705L;

    public SwordDepositNotFoundException() {
        super();
    }

    public SwordDepositNotFoundException(String message) {
        super(message);
    }

    public SwordDepositNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

}
