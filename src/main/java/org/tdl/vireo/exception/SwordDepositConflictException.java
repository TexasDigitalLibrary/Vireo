package org.tdl.vireo.exception;

public class SwordDepositConflictException extends SwordDepositException {

    private static final long serialVersionUID = 6655042738594561428L;

    public SwordDepositConflictException() {
        super();
    }

    public SwordDepositConflictException(String message) {
        super(message);
    }

    public SwordDepositConflictException(String message, Throwable ex) {
        super(message, ex);
    }

}
