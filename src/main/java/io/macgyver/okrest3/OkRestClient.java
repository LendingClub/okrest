package io.macgyver.okrest3;

import io.macgyver.okrest3.compat.OkUriBuilder;
import io.macgyver.okrest3.converter.ConverterRegistry;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import okhttp3.Headers;
import okhttp3.OkHttpClient;

public class OkRestClient {

	OkHttpClient okHttpClient;

	ConverterRegistry registry = null;

	OkRestClient(Builder x) {
	
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
		Preconditions.checkState(registry!=null);
		return registry;
	}
	
	public static class Builder {
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();		
		ConverterRegistry converterRegistry = new ConverterRegistry();
		
		OkHttpClient directlySpecifiedClient=null;
		public Builder withOkHttpClient(OkHttpClient client) {
			directlySpecifiedClient=client;
			return this;
		}
		public Builder withOkHttpClientBuilder(Consumer<OkHttpClient.Builder> okHttpBuilderConsumer) {
			Preconditions.checkState(directlySpecifiedClient==null,"withOkHttpClientBuilder() is mutually exclusive with withOkHttpClient()");
			okHttpBuilderConsumer.accept(okHttpBuilder);
			return this;
		}
		public Builder withBuilder(Consumer<OkRestClient.Builder> okRestBuilderConsumer) {
			okRestBuilderConsumer.accept(this);
			return this;
		}
		public Builder withConverterRegistry(Consumer<ConverterRegistry> converterRegistryConsumer) {
			converterRegistryConsumer.accept(converterRegistry);
			return this;
		}
		public OkRestClient build() {
			OkHttpClient c = okHttpBuilder.build();
			
			OkRestClient restClient = new OkRestClient(this);
			restClient.okHttpClient =c;
			restClient.registry = converterRegistry;
			restClient.getConverterRegistry().markImmutable();
			
			okHttpBuilder=null;
			converterRegistry = null;
			
			Preconditions.checkState(restClient.okHttpClient!=null);
			Preconditions.checkState(restClient.registry!=null);
			
			return restClient;
			
		}
	}
}
