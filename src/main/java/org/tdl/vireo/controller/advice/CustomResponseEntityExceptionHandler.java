package org.tdl.vireo.controller.advice;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static java.lang.String.format;

import javax.persistence.EntityNotFoundException;

import java.nio.file.NoSuchFileException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.tdl.vireo.exception.BatchExportException;
import org.tdl.vireo.exception.SwordDepositBadGatewayException;
import org.tdl.vireo.exception.SwordDepositBadRequestException;
import org.tdl.vireo.exception.SwordDepositConflictException;
import org.tdl.vireo.exception.SwordDepositException;
import org.tdl.vireo.exception.SwordDepositForbiddenException;
import org.tdl.vireo.exception.SwordDepositGatewayTimeoutException;
import org.tdl.vireo.exception.SwordDepositInternalServerErrorException;
import org.tdl.vireo.exception.SwordDepositNotFoundException;
import org.tdl.vireo.exception.SwordDepositNotImplementedException;
import org.tdl.vireo.exception.SwordDepositRequestTimeoutException;
import org.tdl.vireo.exception.SwordDepositServiceUnavailableException;
import org.tdl.vireo.exception.SwordDepositUnauthorizedException;
import org.tdl.vireo.exception.SwordDepositUnprocessableEntityException;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomResponseEntityExceptionHandler.class);

    private static final String PAYLOAD_TOO_LARGE_TEMPLATE = "File exceeds max size %s";
    private static final String CONSTRAINT_VIOLATION_TEMPLATE = "Data constraint violation: %s";
    private static final String DATA_INTEGRITY_TEMPLATE = "Data integrity error: %s";
    private static final String ENTITY_NOT_FOUND_TEMPLATE = "Entity not found: %s";
    private static final String FILE_NOT_FOUND_TEMPLATE = "File not found: %s";
    private static final String SWORD_BAD_GATEWAY_TEMPLATE = "SWORD deposit failed: Bad Gateway";
    private static final String SWORD_BAD_REQUEST_TEMPLATE = "SWORD deposit failed: Bad Request";
    private static final String SWORD_CONFLICT_TEMPLATE = "SWORD deposit failed: Conflict";
    private static final String SWORD_FORBIDDEN_TEMPLATE = "SWORD deposit failed: Forbidden";
    private static final String SWORD_GATEWAY_TIMEOUT_TEMPLATE = "SWORD deposit failed: Gateway Timeout";
    private static final String SWORD_INTERNAL_ERROR_TEMPLATE = "SWORD deposit failed: Internal Server Error";
    private static final String SWORD_NOT_FOUND_TEMPLATE = "SWORD deposit failed: Not Found";
    private static final String SWORD_NOT_IMPLEMENTED_TEMPLATE = "SWORD deposit failed: Not Implemented";
    private static final String SWORD_REQUEST_TIMEOUT_TEMPLATE = "SWORD deposit failed: Request Timeout";
    private static final String SWORD_SERVICE_UNAVAILABLE_TEMPLATE = "SWORD deposit failed: Service Unavailable";
    private static final String SWORD_UNAUTHORIZED_TEMPLATE = "SWORD deposit failed: Unauthorized";
    private static final String SWORD_UNPROCESSABLE_TEMPLATE = "SWORD deposit failed: Unprocessable Entity";

    @Value("${spring.servlet.multipart.max-file-size:20MB}")
    private String maxFileSize;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiResponse handleConstraintViolationException(ConstraintViolationException exception) {
        String message = format(CONSTRAINT_VIOLATION_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiResponse handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = format(DATA_INTEGRITY_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        String message = format(ENTITY_NOT_FOUND_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiResponse handleNoSuchFileException(NoSuchFileException exception) {
        String message = format(FILE_NOT_FOUND_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResponse handleMultipartException(MultipartException exception) {
        String message = format(PAYLOAD_TOO_LARGE_TEMPLATE, maxFileSize);
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositBadGatewayException.class)
    @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
    public ApiResponse handleSwordDepositBadGatewayException(SwordDepositException exception) {
        String message = format(SWORD_BAD_GATEWAY_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse handleSwordDepositBadRequestException(SwordDepositException exception) {
        String message = format(SWORD_BAD_REQUEST_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiResponse handleSwordDepositConflictException(SwordDepositException exception) {
        String message = format(SWORD_CONFLICT_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiResponse handleSwordDepositForbiddenException(SwordDepositException exception) {
        String message = format(SWORD_FORBIDDEN_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositGatewayTimeoutException.class)
    @ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
    public ApiResponse handleSwordDepositGatewayTimeoutException(SwordDepositException exception) {
        String message = format(SWORD_GATEWAY_TIMEOUT_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositInternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleSwordDepositInternalServerErrorException(SwordDepositException exception) {
        String message = format(SWORD_INTERNAL_ERROR_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiResponse handleSwordDepositNotFoundException(SwordDepositException exception) {
        String message = format(SWORD_NOT_FOUND_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositNotImplementedException.class)
    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse handleSwordDepositNotImplementedException(SwordDepositException exception) {
        String message = format(SWORD_NOT_IMPLEMENTED_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositRequestTimeoutException.class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    public ApiResponse handleSwordDepositRequestTimeoutException(SwordDepositException exception) {
        String message = format(SWORD_REQUEST_TIMEOUT_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResponse handleSwordDepositServiceUnavailableException(SwordDepositException exception) {
        String message = format(SWORD_SERVICE_UNAVAILABLE_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ApiResponse handleSwordDepositUnauthorizedException(SwordDepositException exception) {
        String message = format(SWORD_UNAUTHORIZED_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(SwordDepositUnprocessableEntityException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiResponse handleSwordDepositUnprocessableEntityException(SwordDepositException exception) {
        String message = format(SWORD_UNPROCESSABLE_TEMPLATE, exception.getMessage());
        logger.error(message);
        logger.debug(message, exception);
        return ApiResponse.fromException(ERROR, message, exception);
    }

    @ExceptionHandler(BatchExportException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleBatchExportException(BatchExportException exception) {
        String message = exception.getMessage();
        logger.error(message);
        logger.debug(message, exception);

        String responseMessage = "The export failed. Check all required metadata and files, then try to export again.";

        return ApiResponse.fromException(ERROR, responseMessage, exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleExceptions(Exception exception) {
        String message = exception.getMessage();
        logger.error(message);
        logger.debug(message, exception);
        // leaving message null to use front-end default for alert
        return ApiResponse.fromException(ERROR, null, exception);
    }

}
