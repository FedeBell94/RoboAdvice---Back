package it.uiip.digitalgarage.roboadvice.businesslogic.model.response;

import lombok.Getter;

/**
 * This is the default structure of the response given to the frontend.
 */
@Getter
public abstract class AbstractResponse<T> {

    private int response;
    private int errorCode;
    private String errorString;
    private T data;

    protected AbstractResponse(int response, ExchangeError exchangeError, T data) {
        this.response = response;
        this.errorCode = exchangeError == null ? ExchangeError.DEFAULT_ERROR_CODE.getErrorCode()
                : exchangeError.getErrorCode();
        this.errorString = exchangeError == null ? ExchangeError.DEFAULT_ERROR_CODE.getErrorString()
                : exchangeError.getErrorString();
        this.data = data;
    }

}
