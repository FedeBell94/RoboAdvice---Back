package it.uiip.digitalgarage.roboadvice.businesslogic.model.response;

public class SuccessResponse<T> extends AbstractResponse<T> {

	public SuccessResponse(T data) {
		super(1, null, data);
	}

}
