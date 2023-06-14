package org.tdl.vireo.exception;

public class SwordDepositUnprocessableEntityException extends SwordDepositException {

    private static final long serialVersionUID = 816045726698304229L;

    public SwordDepositUnprocessableEntityException() {
        super();
    }

    public SwordDepositUnprocessableEntityException(String message) {
        super(message);
    }

    public SwordDepositUnprocessableEntityException(String message, Throwable ex) {
        super(message, ex);
    }

}
