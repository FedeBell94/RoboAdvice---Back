package it.uiip.digitalgarage.roboadvice.businesslogic.model;

public enum ExchangeError {
	DEFAULT_ERROR_CODE(0, ""), EMAIL_ALREADY_USED(100, "Email already used"),WRONG_EMAIL(101, "Wrong email"),
	WRONG_PASSWORD(102, "Wrong password"), USER_NOT_FOUND(103, "User not found"), SECURITY_ERROR(999, "Security error");

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
