package io.macgyver.okrest.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

public  class JacksonRequestBodyConverter extends RequestBodyConverter {

	ObjectMapper mapper = new ObjectMapper();

	@Override
	public RequestBody convert(Object input) {

		return RequestBody.create(MediaType.parse("application/json"), JsonNode.class
				.cast(input).toString());

	}

	@Override
	public boolean supports(Object input) {
		return input instanceof JsonNode;
	}

}
