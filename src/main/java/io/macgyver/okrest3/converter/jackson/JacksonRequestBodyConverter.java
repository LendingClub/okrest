package io.macgyver.okrest3.converter.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import io.macgyver.okrest3.converter.RequestBodyConverter;

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
