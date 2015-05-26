package io.macgyver.okrest;

import io.macgyver.okrest.compat.OkUriBuilder;
import io.macgyver.okrest.converter.ConverterRegistry;
import io.macgyver.okrest.converter.RequestBodyConverter;
import io.macgyver.okrest.converter.ResponseBodyConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OkRestTarget {

	public static final MediaType APPLICATION_JSON = MediaType
			.parse("application/json");
	protected OkRestClient okRestClient;

	ObjectMapper mapper = new ObjectMapper();

	protected OkUriBuilder uriBuilder = new OkUriBuilder();

	protected Headers headers = Headers.of();

	private ConverterRegistry requestBodyConverterRegistry;

	public class InvocationBuilder {
		Request.Builder okBuilder = new Request.Builder();

		InvocationBuilder(Request.Builder okBuilder) {
			this.okBuilder = okBuilder;

		}

		public OkRestResponse execute() throws IOException {

			Call c = getOkHttpClient().newCall(okBuilder.build());

			return new OkRestResponse(OkRestTarget.this,c.execute());

		}

		public <T> T execute(Class<? extends T> x) throws IOException {

			OkRestResponse okr = execute();
			
			return (T) okr.getBody(x);
			
		
		}

		public Request.Builder okBuilder() {
			return okBuilder;
		}

	

		InvocationBuilder post(Object body) {
			return post(findRequestConverter(body).convert(body));

		}

		InvocationBuilder post(RequestBody body) {
			return new InvocationBuilder(okBuilder.post(body));
		}

		InvocationBuilder get() {
			return new InvocationBuilder(okBuilder.get());
		}

		InvocationBuilder delete() {
			return new InvocationBuilder(okBuilder.delete());
		}

		InvocationBuilder delete(RequestBody body) {
			return new InvocationBuilder(okBuilder.delete());
		}

		InvocationBuilder put(RequestBody body) {
			return new InvocationBuilder(okBuilder.put(body));
		}

		InvocationBuilder put(Object body) {
			return put(findRequestConverter(body).convert(body));
		}

		InvocationBuilder patch(RequestBody body) {
			return new InvocationBuilder(okBuilder.patch(body));
		}

		InvocationBuilder patch(Object body) {
			return patch(findRequestConverter(body).convert(body));
		}

		InvocationBuilder head(RequestBody body) {
			return new InvocationBuilder(okBuilder.head());
		}

		InvocationBuilder head(Object body) {
			return head(findRequestConverter(body).convert(body));
		}

		InvocationBuilder method(String method, RequestBody body) {
			return new InvocationBuilder(okBuilder.method(method, body));
		}

		public InvocationBuilder header(String key, String val) {
			return new InvocationBuilder(okBuilder.header(key, val));
		}

		public InvocationBuilder addHeader(String key, String val) {
			return new InvocationBuilder(okBuilder.addHeader(key, val));
		}

		public InvocationBuilder removeHeader(String key) {
			return new InvocationBuilder(okBuilder.removeHeader(key));
		}
	}
	ResponseBodyConverter findResponseConverter(Class x, Optional<MediaType> t) {
		ResponseBodyConverter converter = getOkRestClient()
				.getConverterRegistry()
				.findResponseConverter(x,t);
		return converter;
	}
	RequestBodyConverter findRequestConverter(Object bodyInput) {
		RequestBodyConverter converter = getOkRestClient()
				.getConverterRegistry()
				.findRequestConverter(bodyInput);
		return converter;
	}

	protected OkRestTarget() {

	}

	public static OkRestTarget create() {
		return new OkRestClient().uri("");
	}

	public OkRestTarget clone() {
		OkRestTarget r = new OkRestTarget();
		r.okRestClient = this.okRestClient;
		r.headers = this.headers.newBuilder().build();
		r.uriBuilder = this.uriBuilder.clone();
		return r;
	}

	public OkRestTarget header(String key, String val) {
		OkRestTarget c = clone();
		c.headers = this.headers.newBuilder().set(key, val).build();
		return c;
	}

	public OkRestTarget addHeader(String key, String val) {
		OkRestTarget c = clone();
		c.headers = this.headers.newBuilder().add(key, val).build();
		return c;
	}

	public OkRestTarget removeHeader(String key) {
		OkRestTarget c = clone();
		c.headers = this.headers.newBuilder().removeAll(key).build();
		return c;
	}

	public Headers getHeaders() {
		return headers;
	}

	public OkRestTarget queryParameter(String key, Object... vals) {

		OkRestTarget copy = clone();

		copy.uriBuilder.queryParam(key, vals);

		return copy;

	}

	public OkRestTarget path(String path) {

		OkRestTarget copy = clone();
		copy.uriBuilder.path(path);
		return copy;
	}

	public InvocationBuilder post(RequestBody body) {
		return request().post(body);
	}

	public InvocationBuilder post(Object data) {
		return request().post(data);
	}

	public InvocationBuilder put(RequestBody body) {
		return request().put(body);
	}

	public InvocationBuilder put(Object data) {
		return request().put(data);
	}

	public InvocationBuilder patch(RequestBody body) {
		return request().patch(body);
	}

	public InvocationBuilder patch(Object data) {
		return request().patch(data);
	}

	public InvocationBuilder head(RequestBody body) {
		return request().head(body);
	}

	public InvocationBuilder head(Object data) {
		return request().head(data);
	}

	public InvocationBuilder get() {
		return request().get();
	}

	public InvocationBuilder delete() {
		return request().delete();
	}

	public OkRestTarget uri(URI uri) {
		return url(uri);
	}
	public OkRestTarget uri(String uri) {
		return url(uri);
	}
	public OkRestTarget uri(URL uri) {
		return url(uri);
	}
	
	public OkRestTarget url(URL url) {
		return url(url.toExternalForm());
	}
	public OkRestTarget url(URI uri) {
		try {
		return url(uri.toURL());
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public OkRestTarget url(String url) {

		OkRestTarget copy = clone();
		copy.uriBuilder.uri(url);
		return copy;
	}

	public InvocationBuilder request() {

		Request.Builder rb = new Request.Builder();
		rb = rb.url(uriBuilder.build().toString());

		InvocationBuilder b = new InvocationBuilder(rb);

		b.okBuilder.headers(headers);

		return b;
	}

	public String getUrl() {
		return uriBuilder.build().toString();
	}

	public OkUriBuilder getOkUriBuilder() {
		return uriBuilder;
	}

	public OkRestClient getOkRestClient() {
		return okRestClient;
	}

	public OkHttpClient getOkHttpClient() {
		return okRestClient.okHttpClient;
	}

	private Call newCall(Request.Builder b) {
		return getOkHttpClient().newCall(b.build());
	}

	/*
	 * protected String toUrlWithQueryString(Multimap<String, String> extras) {
	 * try { if (url == null) { throw new IllegalStateException("invalid url: "
	 * + url); } StringBuilder sb = new StringBuilder(); sb.append(url);
	 * 
	 * if (!queryParameters.isEmpty()) { int count = 0;
	 * 
	 * for (Entry<String, String> x : queryParameters.entries()) { if (count ==
	 * 0) { sb.append("?"); } else { sb.append("&"); }
	 * sb.append(URLEncoder.encode(x.getKey(), "UTF8")); sb.append("=");
	 * sb.append(URLEncoder.encode(x.getValue(), "UTF8"));
	 * 
	 * count++; }
	 * 
	 * if (extras != null) { for (Entry<String, String> x : extras.entries()) {
	 * 
	 * if (count == 0) { sb.append("?"); } else { sb.append("&"); }
	 * sb.append(URLEncoder.encode(x.getKey(), "UTF8")); sb.append("=");
	 * sb.append(URLEncoder.encode(x.getValue(), "UTF8"));
	 * 
	 * count++; } }
	 * 
	 * } return sb.toString(); } catch (UnsupportedEncodingException e) { throw
	 * new IllegalArgumentException(e); } }
	 */

	public URI toURI() {
		return uriBuilder.build();
	}
}
