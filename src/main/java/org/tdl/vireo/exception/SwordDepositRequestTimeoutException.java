package org.tdl.vireo.exception;

public class SwordDepositRequestTimeoutException extends SwordDepositException {

    private static final long serialVersionUID = 5174628595492299267L;

    public SwordDepositRequestTimeoutException() {
        super();
    }

    public SwordDepositRequestTimeoutException(String message) {
        super(message);
    }

    public SwordDepositRequestTimeoutException(String message, Throwable ex) {
        super(message, ex);
    }

}
