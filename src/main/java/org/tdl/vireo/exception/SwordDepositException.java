package org.tdl.vireo.exception;

public class SwordDepositException extends RuntimeException {

    private static final long serialVersionUID = -3564243098286926246L;

    public SwordDepositException() {

    }

    public SwordDepositException(String message) {
        super(message);
    }

    public SwordDepositException(String message, Throwable ex) {
        super(message, ex);
    }

}
