package org.tdl.vireo.exception;

public class SwordDepositInternalServerErrorException extends SwordDepositException {

    private static final long serialVersionUID = -1475151059148205394L;

    public SwordDepositInternalServerErrorException() {
        super();
    }

    public SwordDepositInternalServerErrorException(String message) {
        super(message);
    }

    public SwordDepositInternalServerErrorException(String message, Throwable ex) {
        super(message, ex);
    }

}
