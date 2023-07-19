package org.tdl.vireo.exception;

public class SwordDepositNotImplementedException extends SwordDepositException {

    private static final long serialVersionUID = 7638867056316249437L;

    public SwordDepositNotImplementedException() {
        super();
    }

    public SwordDepositNotImplementedException(String message) {
        super(message);
    }

    public SwordDepositNotImplementedException(String message, Throwable ex) {
        super(message, ex);
    }

}
