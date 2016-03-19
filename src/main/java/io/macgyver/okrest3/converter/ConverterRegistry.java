package io.macgyver.okrest3.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConverterRegistry {

	boolean immutable=false;
	
	Logger logger = LoggerFactory.getLogger(ConverterRegistry.class);
	List<RequestBodyConverter> requestConverters = Lists
			.newCopyOnWriteArrayList();
	List<ResponseBodyConverter> responseConverters = Lists
			.newCopyOnWriteArrayList();
	private static ConverterRegistry defaultRegistry = new ConverterRegistry();

	private ResponseErrorHandler defaultResponseErrorHandler = new DefaultResponseErrorHandler();

	public ConverterRegistry() {

		requestConverters.add(new PassThroughRequestBodyConverter());
		addRequestBodyConverter(
				"io.macgyver.okrest3.converter.jackson.JacksonRequestBodyConverter",
				false); // do not fail if jackson is not available at runtime
		requestConverters.add(new StringRequestBodyConverter());
		requestConverters.add(new FileRequestBodyConverter());
		requestConverters.add(new ByteArrayRequestBodyConverter());

		addResponseBodyConverter(
				"io.macgyver.okrest3.converter.jackson.JacksonResponseBodyConverter",
				false);  // do not fail if jackson is not available at runtime
		responseConverters.add(new StringResponseBodyConverter());
		responseConverters.add(new ByteArrayResponseBodyConverter());
		responseConverters.add(new InputStreamResponseBodyConverter());
		responseConverters.add(new ReaderResponseBodyConverter());
	}

	public static ConverterRegistry defaultRegistry() {
		return defaultRegistry;
	}

	public static ConverterRegistry newRegistry() {
		return new ConverterRegistry();
	}

	public void markImmutable() {
		immutable=true;
	}
	public void assertNotImmutable() {
		Preconditions.checkState(immutable==false,"ConverterRegistry is immutable after construction");
	}
	public void addResponseBodyConverter(ResponseBodyConverter c) {
		assertNotImmutable();
		Preconditions.checkNotNull(c);
		responseConverters.add(c);
	}

	public void addRequestBodyConverter(RequestBodyConverter c) {
		assertNotImmutable();
		Preconditions.checkNotNull(c);
		requestConverters.add(c);
	}

	public void addRequestBodyConverter(String s, boolean failOnError) {
		assertNotImmutable();
		try {
			RequestBodyConverter c = (RequestBodyConverter) Class.forName(s)
					.newInstance();
			addRequestBodyConverter(c);
		} catch (Throwable e) {
			if (failOnError) {
				throw new IllegalArgumentException(e);
			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("could not load converter: " + s, e);
				}
				if (logger.isWarnEnabled() && !logger.isDebugEnabled()) {
					logger.info("could not load converter: " + s);
				}
			}
		}
	}

	public void addResponseBodyConverter(String s, boolean failOnError) {
		assertNotImmutable();
		try {
			ResponseBodyConverter c = (ResponseBodyConverter) Class.forName(s)
					.newInstance();
			addResponseBodyConverter(c);
		} catch (Throwable e) {
			if (failOnError) {
				throw new IllegalArgumentException(e);
			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("could not load converter: " + s, e);
				}
				if (logger.isWarnEnabled() && !logger.isDebugEnabled()) {
					logger.info("could not load converter: " + s);
				}
			}
		}
	}

	public RequestBodyConverter findRequestConverter(Object input) {
		
		
		for (RequestBodyConverter c : requestConverters) {
			if (c.supports(input)) {
				return c;
			}
		}
		throw new IllegalArgumentException("could not find type converter for "
				+ input.getClass());
	}

	public ResponseBodyConverter findResponseConverter(Class<?> desiredType,
			Optional<MediaType> mt) {
		for (ResponseBodyConverter c : responseConverters) {
			if (c.supports(desiredType, mt)) {
				return c;
			}
		}
		throw new IllegalArgumentException("coult not find converter for: "
				+ desiredType);
	}

	public static class ByteArrayRequestBodyConverter extends
			RequestBodyConverter {
		@Override
		public boolean supports(Object input) {
			return input instanceof byte[];
		}

		@Override
		public RequestBody convert(Object input) {

			return RequestBody.create(null, (byte[]) input);

		}
	}

	public static class FileRequestBodyConverter extends RequestBodyConverter {
		@Override
		public boolean supports(Object input) {
			return input instanceof File;
		}

		@Override
		public RequestBody convert(Object input) {

			return RequestBody.create(null, (File) input);

		}
	}

	public static class StringResponseBodyConverter extends
			ResponseBodyConverter {

		@Override
		public boolean supports(Class<?> t, Optional<MediaType> mediaType) {
			return String.class.isAssignableFrom(t);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Response r, Class<? extends T> t)
				throws IOException {
			return (T) r.body().string();
		}

	}

	public static class InputStreamResponseBodyConverter extends
			ResponseBodyConverter {

		@Override
		public boolean supports(Class<? extends Object> t,
				Optional<MediaType> mediaType) {
			return InputStream.class.isAssignableFrom(t);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Response r, Class<? extends T> t)
				throws IOException {

			return (T) r.body().byteStream();
		}

	}

	public static class ByteArrayResponseBodyConverter extends
			ResponseBodyConverter {

		@Override
		public boolean supports(Class<? extends Object> t,
				Optional<MediaType> mediaType) {
			return byte[].class.isAssignableFrom(t);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Response r, Class<? extends T> t)
				throws IOException {
			return (T) r.body().bytes();
		}

	}

	public static class ReaderResponseBodyConverter extends
			ResponseBodyConverter {

		@Override
		public boolean supports(Class<? extends Object> t,
				Optional<MediaType> mediaType) {
			return Reader.class.isAssignableFrom(t);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Response r, Class<? extends T> t) throws IOException
			 {
			return ((T) r.body().charStream());
		}

	}

	public static class StringRequestBodyConverter extends RequestBodyConverter {

		@Override
		public boolean supports(Object input) {
			return input instanceof String;
		}

		@Override
		public RequestBody convert(Object input) {

			return RequestBody.create(null, (String) input);

		}

	}

	public static class PassThroughRequestBodyConverter extends
			RequestBodyConverter {

		@Override
		public boolean supports(Object input) {
			return input instanceof RequestBody;
		}

		@Override
		public RequestBody convert(Object input) {

			return (RequestBody) input;

		}

	}

	public ResponseErrorHandler getDefaultResponseErrorHandler() {
		return defaultResponseErrorHandler;
	}

	public void setDefaultResponseErrorHandler(ResponseErrorHandler h) {
		Preconditions.checkNotNull(h);
		this.defaultResponseErrorHandler = h;
	}

	public ResponseErrorHandler findErrorHandler(Class<? extends Object> clazz) {
		// Eventually we may want to allow for custom error handlers to be registered
		return defaultResponseErrorHandler;
	}
	
	
}
