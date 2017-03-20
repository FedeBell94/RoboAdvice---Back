package it.uiip.digitalgarage.roboadvice.businesslogic.model.response;

public class ErrorResponse extends AbstractResponse<String> {

	public ErrorResponse(ExchangeError exchangeError) {
		super(0, exchangeError, null);
	}

	public ErrorResponse(String s){
		super(0, ExchangeError.DEFAULT_ERROR_CODE, s);
	}

}
