package org.tdl.vireo.exception;

public class SwordDepositForbiddenException extends SwordDepositException {

    private static final long serialVersionUID = 5137876785611242339L;

    public SwordDepositForbiddenException() {
        super();
    }

    public SwordDepositForbiddenException(String message) {
        super(message);
    }

    public SwordDepositForbiddenException(String message, Throwable ex) {
        super(message, ex);
    }

}
