package it.uiip.digitalgarage.roboadvice.businesslogic.model.response;

public class ErrorResponse extends AbstractResponse<Object> {

	public ErrorResponse(ExchangeError exchangeError) {
		super(0, exchangeError, null);
	}

}
