package io.macgyver.okrest;

import java.io.IOException;

import com.squareup.okhttp.Response;

public class OkRestException extends RuntimeException {

	public static final int EXCEPTION_STATUS_CODE=400;
	private static final long serialVersionUID = 1L;
	int statusCode;
	
	protected OkRestException(Throwable t) {
		super(formatMessage(EXCEPTION_STATUS_CODE,t.toString()),t);
		this.statusCode = EXCEPTION_STATUS_CODE;
	}
	
	public OkRestException(int statusCode) {
		super(formatMessage(statusCode,null));
		this.statusCode = statusCode;
	}
	public OkRestException(int statusCode, String message) {
		super(formatMessage(statusCode,message));
		this.statusCode = statusCode;
	}
	public static OkRestException fromResponse(Response r) {
		OkRestException exception = new OkRestException(r.code());
		return exception;
	}
	public int getStatusCode() {
		return statusCode;
	}
	
	public static String formatMessage(int statusCode, String message) {
		String s= "statusCode="+statusCode;
		if (message!=null) {
			s = s+": "+message;
		}
		return s;
	}
	
}
