package it.uiip.digitalgarage.roboadvice.businesslogic.model;

public enum ExchangeError {
	DEFAULT_ERROR_CODE, WRONG_EMAIL, WRONG_PASSWORD;

	public int getErrorCode() {
		switch (this) {
		case DEFAULT_ERROR_CODE:
			return 0;
		case WRONG_EMAIL:
			return 101;
		case WRONG_PASSWORD:
			return 102;
		default:
			return 0;
		}
	}
}
