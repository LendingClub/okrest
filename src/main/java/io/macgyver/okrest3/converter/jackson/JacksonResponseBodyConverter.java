package io.macgyver.okrest3.converter.jackson;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import okhttp3.MediaType;
import okhttp3.Response;

import io.macgyver.okrest3.converter.ResponseBodyConverter;

public class JacksonResponseBodyConverter extends ResponseBodyConverter {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean supports(Class <? extends Object>t, Optional<MediaType> mediaType) {
		return JsonNode.class.isAssignableFrom(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Response r, Class<? extends T> t) throws IOException {
		
		try (InputStream is = r.body().byteStream()) {
			return (T) mapper.readTree(is);
		}
		finally {
			if (r!=null) {
				r.body().close();
			}
		}
	
	}

}
