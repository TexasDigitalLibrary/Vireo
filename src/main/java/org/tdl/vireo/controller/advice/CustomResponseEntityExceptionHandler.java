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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
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

    @Value("${spring.servlet.multipart.max-file-size:20MB}")
    private String maxFileSize;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse handleConstraintViolationException(ConstraintViolationException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleNoSuchFileExceptionn(NoSuchFileException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ApiResponse handleMultipartException(MultipartException exception) {
        String message = format(PAYLOAD_TOO_LARGE_TEMPLATE, maxFileSize);
        logger.error(exception.getMessage());
        logger.debug(message, exception);
        return new ApiResponse(ERROR, message);
    }

    @ExceptionHandler(SwordDepositBadGatewayException.class)
    @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public ApiResponse handleSwordDepositBadGatewayException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleSwordDepositBadRequestException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse handleSwordDepositConflictException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiResponse handleSwordDepositForbiddenException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositGatewayTimeoutException.class)
    @ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public ApiResponse handleSwordDepositGatewayTimeoutException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositInternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleSwordDepositInternalServerErrorException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleSwordDepositNotFoundException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositNotImplementedException.class)
    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
    @ResponseBody
    public ApiResponse handleSwordDepositNotImplementedException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        logger.error(exception.getMessage());
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositRequestTimeoutException.class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    @ResponseBody
    public ApiResponse handleSwordDepositRequestTimeoutException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ApiResponse handleSwordDepositServiceUnavailableException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleSwordDepositUnauthorizedException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositUnprocessableEntityException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ApiResponse handleSwordDepositUnprocessableEntityException(SwordDepositException exception) {
        logger.error(exception.getMessage());
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

}
