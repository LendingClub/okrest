package io.macgyver.okrest;

import io.macgyver.okrest.converter.ResponseBodyConverter;

import java.io.IOException;

import com.google.common.base.Optional;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

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
			MediaType mt = contentType!=null ? MediaType.parse(response.header("Content-type")): null;
			ResponseBodyConverter c = okRestTarget.findResponseConverter(clazz,Optional.fromNullable(mt));
			return c.convert(response, clazz);
			}
			catch (IOException e) {
				throw new OkRestException(e);
			}
		}
		else {
			throw OkRestException.fromResponse(response);
		}
	}
}
