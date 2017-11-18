package io.macgyver.okrest3.converter;

import okhttp3.Response;

import io.macgyver.okrest3.OkRestException;

public abstract class ResponseErrorHandler {

	public abstract <T> T handleError(Response response, Class<? extends T> clazz) throws OkRestException;

}
