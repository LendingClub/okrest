package io.macgyver.okrest3;

import java.io.IOException;

import com.google.common.base.Optional;
import okhttp3.MediaType;
import okhttp3.Response;

import io.macgyver.okrest3.converter.ResponseBodyConverter;

public class OkRestResponse {

	Response response;
	OkRestTarget okRestTarget;

	public OkRestResponse(OkRestTarget target, Response r) {
		okRestTarget = target;
		this.response = r;
	}

	public Response response() {
		return response;
	}

	public <T> T getBody(Class<? extends T> clazz) throws OkRestException {

		Response response = response();

		if (response.isSuccessful()) {
			try {
				String contentType = response.header("content-type");
				MediaType mt = contentType != null ? MediaType.parse(response
						.header("Content-type")) : null;
				ResponseBodyConverter c = okRestTarget.findResponseConverter(
						clazz, Optional.fromNullable(mt));
				return c.convert(response, clazz);
			} catch (IOException e) {
				throw new OkRestException(e);
			}
		} else {
			return okRestTarget.getOkRestClient().getConverterRegistry().findErrorHandler(clazz).handleError(response, clazz);
		}
	}
}
