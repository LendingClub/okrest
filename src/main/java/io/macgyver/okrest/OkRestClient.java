package io.macgyver.okrest;

import io.macgyver.okrest.compat.OkUriBuilder;
import io.macgyver.okrest.converter.ConverterRegistry;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;

public class OkRestClient {

	OkHttpClient okHttpClient;

	ConverterRegistry registry = ConverterRegistry.newRegistry();

	public OkRestClient() {
		okHttpClient = new OkHttpClient();
	}
	
	public OkRestClient(OkHttpClient c) {
		this.okHttpClient = c;
	}

	public OkRestTarget url(String url) {
		return uri(url);
	}
	public OkRestTarget url(URI url) {
		return uri(url);
	}
	public OkRestTarget url(URL url) {
		return uri(url);
	}
	public OkRestTarget uri(URL url) {
		return uri(url.toExternalForm());
	}

	public OkRestTarget uri(URI uri) {
		try {
			return uri(uri.toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public OkRestTarget uri(String uri) {

		OkRestTarget r = new OkRestTarget();
		r.okRestClient = this;
		r.uriBuilder = new OkUriBuilder().uri(uri);
		r.headers = Headers.of();
		return r;

	}

	public OkHttpClient getOkHttpClient() {
		return okHttpClient;
	}

	public ConverterRegistry getConverterRegistry() {
		return registry;
	}
}
