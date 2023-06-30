package org.tdl.vireo.controller.advice;

import static edu.tamu.weaver.response.ApiStatus.ERROR;

import edu.tamu.weaver.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.tdl.vireo.exception.SwordDepositBadRequestException;
import org.tdl.vireo.exception.SwordDepositConflictException;
import org.tdl.vireo.exception.SwordDepositException;
import org.tdl.vireo.exception.SwordDepositForbiddenException;
import org.tdl.vireo.exception.SwordDepositInternalServerErrorException;
import org.tdl.vireo.exception.SwordDepositNotFoundException;
import org.tdl.vireo.exception.SwordDepositUnauthorizedException;
import org.tdl.vireo.exception.SwordDepositUnprocessableEntityException;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ApiResponse handleMultipartException(MultipartException exception) {
        logger.debug("File size limit exceeded", exception);
        return new ApiResponse(ERROR, "File size limit exceeded");
    }

    @ExceptionHandler(SwordDepositBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleSwordDepositBadRequestException(SwordDepositException exception) {
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
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositInternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleSwordDepositInternalServerErrorException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleSwordDepositNotFoundException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleSwordDepositUnauthorizedException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

    @ExceptionHandler(SwordDepositUnprocessableEntityException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ApiResponse handleSwordDepositUnprocessableEntityException(SwordDepositException exception) {
        logger.debug(exception.getMessage(), exception);
        return new ApiResponse(ERROR, exception.getMessage());
    }

}
