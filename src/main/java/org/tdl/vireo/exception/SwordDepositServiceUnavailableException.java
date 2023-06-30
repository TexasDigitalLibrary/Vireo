package org.tdl.vireo.exception;

public class SwordDepositServiceUnavailableException extends SwordDepositException {

    private static final long serialVersionUID = 6133371528307660865L;

    public SwordDepositServiceUnavailableException() {
        super();
    }

    public SwordDepositServiceUnavailableException(String message) {
        super(message);
    }

    public SwordDepositServiceUnavailableException(String message, Throwable ex) {
        super(message, ex);
    }

}
