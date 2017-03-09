package it.uiip.digitalgarage.roboadvice.businesslogic.model.response;

public enum ExchangeError {
	DEFAULT_ERROR_CODE(0, ""), EMAIL_ALREADY_USED(100, "Email already used");

	private final int errorCode;
	private final String errorString;

	ExchangeError(int errorCode, String errorString){
		this.errorCode = errorCode;
		this.errorString = errorString;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public String getErrorString(){
		return this.errorString;
	}
}
