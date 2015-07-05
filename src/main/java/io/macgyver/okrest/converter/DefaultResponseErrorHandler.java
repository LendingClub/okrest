package io.macgyver.okrest.converter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.okrest.OkRestException;

import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.Response;

public class DefaultResponseErrorHandler extends ResponseErrorHandler {

	Logger logger = LoggerFactory.getLogger(DefaultResponseErrorHandler.class);

	@Override
	public <T> T handleError(Response response, Class<? extends T> clazz)
			throws OkRestException {

		OkRestException x = OkRestException.fromResponse(response);
		try {
			String s = CharStreams.toString(response.body().charStream());
			x.setErrorResponseBody(s);

		} catch (Exception ignore) {
			// ignore this
		}
		throw x;
	}

}
