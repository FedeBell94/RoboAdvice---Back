package it.uiip.digitalgarage.roboadvice.businesslogic.exception;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    private static final Log LOGGER = LogFactory.getLog(GlobalControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BadRequestException.class)
    public AbstractResponse handleBadRequestException(BadRequestException e) {
        LOGGER.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}