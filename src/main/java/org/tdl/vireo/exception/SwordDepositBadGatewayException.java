package org.tdl.vireo.exception;

public class SwordDepositBadGatewayException extends SwordDepositException {

    private static final long serialVersionUID = 3008114452971644768L;

    public SwordDepositBadGatewayException() {
        super();
    }

    public SwordDepositBadGatewayException(String message) {
        super(message);
    }

    public SwordDepositBadGatewayException(String message, Throwable ex) {
        super(message, ex);
    }

}
