package io.macgyver.okrest;

import java.io.IOException;

import com.squareup.okhttp.Response;

public class OkRestException extends IOException {

	int statusCode;
	public OkRestException(int statusCode) {
		super("statusCode="+statusCode);
	}
	public static OkRestException fromResponse(Response r) {
		OkRestException exception = new OkRestException(r.code());
		return exception;
	}
	public int getStatusCode() {
		return statusCode;
	}
}
