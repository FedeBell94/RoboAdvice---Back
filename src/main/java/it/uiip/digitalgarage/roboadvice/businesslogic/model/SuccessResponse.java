package it.uiip.digitalgarage.roboadvice.businesslogic.model;

public class SuccessResponse<T> extends AbstractResponse<T> {

	public SuccessResponse(T data) {
		super(1, null, data);
	}

}
