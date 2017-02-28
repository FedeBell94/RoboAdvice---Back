package it.uiip.digitalgarage.roboadvice.businesslogic.model;

public abstract class AbstractResponse<T> {

	private int response;
	private int errorCode;
	private T data;

	protected AbstractResponse(int response, ExchangeError exchangeError, T data) {
		this.response = response;
		this.errorCode = exchangeError == null ? ExchangeError.DEFAULT_ERROR_CODE.getErrorCode()
				: exchangeError.getErrorCode();
		this.data = data;
	}

	public int getResponse() {
		return this.response;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public T getData() {
		return this.data;
	}

}
