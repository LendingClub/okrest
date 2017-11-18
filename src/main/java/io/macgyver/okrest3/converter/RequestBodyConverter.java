package io.macgyver.okrest3.converter;
import okhttp3.RequestBody;

public abstract class RequestBodyConverter {

	
	public abstract boolean supports(Object input);
	public abstract RequestBody convert(Object input);
}
