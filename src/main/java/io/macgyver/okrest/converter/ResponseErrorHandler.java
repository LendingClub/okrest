package io.macgyver.okrest.converter;

import io.macgyver.okrest.OkRestException;

import com.squareup.okhttp.Response;

public abstract class ResponseErrorHandler {

	public abstract <T> T handleError(Response response, Class<? extends T> clazz) throws OkRestException;

}
