package io.macgyver.okrest.converter;
import com.squareup.okhttp.RequestBody;

public abstract class RequestBodyConverter {

	
	public abstract boolean supports(Object input);
	public abstract RequestBody convert(Object input);
}
