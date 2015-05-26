package io.macgyver.okrest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import io.macgyver.okrest.compat.OkUriBuilder;
import io.macgyver.okrest.converter.ConverterRegistry;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;

public class OkRestClient {

	OkHttpClient okHttpClient;

	ConverterRegistry registry = ConverterRegistry.defaultRegistry();

	public OkRestClient() {
		okHttpClient = new OkHttpClient();
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
