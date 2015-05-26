package io.macgyver.okrest;

import io.macgyver.okrest.converter.ResponseBodyConverter;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	public <T> T getBody(Class<? extends T> clazz) throws IOException {

		Response response = response();

		if (response.isSuccessful()) {
			String contentType = response.header("content-type");
			MediaType mt = contentType!=null ? MediaType.parse(response.header("Content-type")): null;
			ResponseBodyConverter c = okRestTarget.findResponseConverter(clazz,Optional.fromNullable(mt));
			return c.convert(response, clazz);
		}
		else {
			throw OkRestException.fromResponse(response);
		}
	}
}
