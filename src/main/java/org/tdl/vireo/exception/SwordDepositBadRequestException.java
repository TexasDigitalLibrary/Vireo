package org.tdl.vireo.exception;

public class SwordDepositBadRequestException extends SwordDepositException {

    private static final long serialVersionUID = 206711142922120313L;

    public SwordDepositBadRequestException() {
        super();
    }

    public SwordDepositBadRequestException(String message) {
        super(message);
    }

    public SwordDepositBadRequestException(String message, Throwable ex) {
        super(message, ex);
    }

}
