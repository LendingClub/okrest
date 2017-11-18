package io.macgyver.okrest3.converter;

import java.io.IOException;

import com.google.common.base.Optional;
import okhttp3.MediaType;
import okhttp3.Response;

public abstract class ResponseBodyConverter {

	public abstract boolean supports(Class<? extends Object> t, Optional<MediaType> mediaType);
	
	public abstract <T> T convert(Response r, Class<? extends T> t) throws IOException;
	
}
