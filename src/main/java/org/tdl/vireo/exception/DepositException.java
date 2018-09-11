package org.tdl.vireo.exception;

public class DepositException extends RuntimeException {

    private static final long serialVersionUID = -3564243098286926246L;

    public DepositException() {
        super();
    }

    public DepositException(String message) {
        super(message);
    }

    public DepositException(String message, Throwable ex) {
        super(message, ex);
    }

}
