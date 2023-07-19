package org.tdl.vireo.exception;

public class SwordDepositUnauthorizedException extends SwordDepositException {

    private static final long serialVersionUID = -3845725334845165480L;

    public SwordDepositUnauthorizedException() {
        super();
    }

    public SwordDepositUnauthorizedException(String message) {
        super(message);
    }

    public SwordDepositUnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }

}
