/**
 * Copyright 2017 Lending Club, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lendingclub.okrest3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import org.lendingclub.okrest3.compat.OkUriBuilder;
import org.lendingclub.okrest3.converter.RequestBodyConverter;
import org.lendingclub.okrest3.converter.ResponseBodyConverter;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkRestTarget {

	public static final MediaType APPLICATION_JSON = MediaType
			.parse("application/json");
	protected OkRestClient okRestClient;

	protected OkUriBuilder uriBuilder = new OkUriBuilder();

	protected Headers headers = Headers.of();

	public class InvocationBuilder {
		Request.Builder okBuilder = new Request.Builder();

		InvocationBuilder(Request.Builder okBuilder) {
			this.okBuilder = okBuilder;

		}

		public OkRestResponse execute() {

			Response response = null;
			try {
				Call c = getOkHttpClient().newCall(okBuilder.build());
				response = c.execute();
				return new OkRestResponse(OkRestTarget.this, response);
			} catch (IOException e) {
				if (response!=null) {
					response.body().close();
				}
				throw new OkRestException(e);
			}

		}

		public <T> T execute(Class<? extends T> x) {

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

	ResponseBodyConverter findResponseConverter(Class<?> x,
			Optional<MediaType> t) {
		ResponseBodyConverter converter = getOkRestClient()
				.getConverterRegistry().findResponseConverter(x, t);
		return converter;
	}

	RequestBodyConverter findRequestConverter(Object bodyInput) {
		RequestBodyConverter converter = getOkRestClient()
				.getConverterRegistry().findRequestConverter(bodyInput);
		return converter;
	}

	protected OkRestTarget() {

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
	public OkRestTarget accept(String val) {
		return header("Accept",val);
	}
	
	public OkRestTarget contentType(String val) {
		return header("Content-type",val);
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

	public OkRestTarget queryParam(Object ...vals) {
		
		if (vals==null || vals.length==0) {
			return this;
		}
		
		OkRestTarget copy = clone();
		
		
		if (vals!=null && vals.length %2 !=0) {
			throw new IllegalArgumentException("queryParameters() must have an even number of key value pair arguments");
		}
		
		for (int i=0; vals!=null &&  i<vals.length; i+=2) {
			String key = Objects.toString(vals[i],"");
			String val = Objects.toString(vals[i+1],"");
			if (Strings.isNullOrEmpty(key)) {
				throw new IllegalArgumentException("query parameter key cannot be null or empty");
			}
			copy.uriBuilder.queryParam(key,val);
		}
		return copy;
	}
	
	public OkRestTarget queryParam(Map<?,?> m) {
		if (m==null) {
			return this;
		}
		OkRestTarget copy = clone();
		
		m.entrySet().forEach(entry -> {
			String stringKey = Objects.toString(entry.getKey(),"");
			if (Strings.isNullOrEmpty(stringKey)) {
				throw new IllegalArgumentException("query parameter key cannot be null or empty");
			}
			String stringVal = Objects.toString(entry.getValue(),"");
			copy.uriBuilder.queryParam(stringKey, stringVal);
		});
		
		return copy;
		
	}
	
	

	public OkRestTarget queryParamMultiValue(String key, Object... vals) {

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
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public OkRestTarget url(String url) {

		OkRestTarget copy = clone();
		copy.uriBuilder.uri(url);
		return copy;
	}

	protected InvocationBuilder request() {

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
		Preconditions.checkState(okRestClient!=null,"OkRestClient not set");
		return okRestClient.okHttpClient;
	}

	protected Call newCall(Request.Builder b) {
		return getOkHttpClient().newCall(b.build());
	}

	public URI toURI() {
		return uriBuilder.build();
	}
}
