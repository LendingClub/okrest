package io.macgyver.okrest.converter;

import java.io.IOException;

import com.google.common.base.Optional;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

public abstract class ResponseBodyConverter {

	public abstract boolean supports(Class<? extends Object> t, Optional<MediaType> mediaType);
	
	public abstract <T> T convert(Response r, Class<? extends T> t) throws IOException;
	
}
