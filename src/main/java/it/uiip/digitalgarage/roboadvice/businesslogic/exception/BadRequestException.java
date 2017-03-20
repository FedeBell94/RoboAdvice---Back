package it.uiip.digitalgarage.roboadvice.businesslogic.exception;

/**
 * Exception thrown from controllers when the input parameters of the request are malformed.
 */
public class BadRequestException extends Exception {

    public BadRequestException(){
        super();
    }

    public BadRequestException(String s){
        super(s);
    }
}
