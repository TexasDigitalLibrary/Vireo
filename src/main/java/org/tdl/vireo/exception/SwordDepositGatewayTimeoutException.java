package org.tdl.vireo.exception;

public class SwordDepositGatewayTimeoutException extends SwordDepositException {

    private static final long serialVersionUID = 3614666808115728143L;

    public SwordDepositGatewayTimeoutException() {
        super();
    }

    public SwordDepositGatewayTimeoutException(String message) {
        super(message);
    }

    public SwordDepositGatewayTimeoutException(String message, Throwable ex) {
        super(message, ex);
    }

}
