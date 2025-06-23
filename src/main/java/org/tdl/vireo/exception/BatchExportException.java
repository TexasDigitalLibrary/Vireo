package org.tdl.vireo.exception;

public class BatchExportException extends RuntimeException {

    public BatchExportException(String message) {
        super(message);
    }

    public BatchExportException(String message, Throwable cause) {
        super(message, cause);
    }

}
