package io.macgyver.okrest.converter.jackson;

import io.macgyver.okrest.converter.ResponseBodyConverter;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

public class JacksonResponseBodyConverter extends ResponseBodyConverter {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean supports(Class t, Optional<MediaType> mediaType) {
		return JsonNode.class.isAssignableFrom(t);
	}

	@Override
	public <T> T convert(Response r, Class<? extends T> t) throws IOException {
		try (InputStream is = r.body().byteStream()) {
			return (T) mapper.readTree(is);
		}
		
	
	}

}
