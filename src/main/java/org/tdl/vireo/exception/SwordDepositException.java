package org.tdl.vireo.exception;

public class SwordDepositException extends DepositException {

    private static final long serialVersionUID = 446759184838773536L;

    public SwordDepositException() {
        super();
    }

    public SwordDepositException(String message) {
        super(message);
    }

    public SwordDepositException(String message, Throwable ex) {
        super(message, ex);
    }

}
