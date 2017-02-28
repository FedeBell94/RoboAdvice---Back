package it.uiip.digitalgarage.roboadvice.businesslogic.model;

public class ErrorResponse extends AbstractResponse<Object> {

	public ErrorResponse(ExchangeError exchangeError) {
		super(0, exchangeError, null);
	}

}
