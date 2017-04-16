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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import org.lendingclub.okrest3.compat.OkUriBuilder;
import org.lendingclub.okrest3.converter.ConverterRegistry;
import org.lendingclub.okrest3.converter.ConverterRegistryConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class OkRestClient {

	OkHttpClient okHttpClient;

	ConverterRegistry registry = null;

	static AtomicLong constructorCount= new AtomicLong(0);
	OkRestClient() {

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
		Preconditions.checkState(okHttpClient != null, "okHttpClient not set");
		return okHttpClient;
	}

	public ConverterRegistry getConverterRegistry() {
		Preconditions.checkState(registry != null, "ConverterRegistry not set");
		return registry;
	}

	public static class DiagnosticStackTrace extends RuntimeException {
		
	}
	public static class Builder {
		
		
		boolean buildCalled = false;
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
		ConverterRegistry converterRegistry = new ConverterRegistry();

		OkHttpClient directlySpecifiedClient = null;

		public Builder withOkHttpClient(OkHttpClient client) {
			directlySpecifiedClient = client;
			return this;
		}

		public Builder withOkHttpClientConfig(OkHttpClientConfigurer cfg) {
			cfg.accept(okHttpClientBuilder);
			return this;
		}

		
		public Builder withInterceptor(Interceptor interceptor) {
			withOkHttpClientConfig(it -> it.addInterceptor(interceptor));
			return this;
		}

		
		public Builder disableCertificateVerification() {
			return withOkHttpClientConfig(cfg -> {
				cfg.hostnameVerifier(new TLSUtil.TrustAllHostnameVerifier());
				cfg.sslSocketFactory(TLSUtil.createTrustAllSSLContext().getSocketFactory());
			});
		}

		public Builder withOkRestClientConfig(OkRestClientConfigurer okRestBuilderConsumer) {
			okRestBuilderConsumer.accept(this);
			return this;
		}

		public Builder withOkHttpClientBuilder(OkHttpClient.Builder builder) {
			this.okHttpClientBuilder = builder;
			this.directlySpecifiedClient = null;
			return this;
		}

		public Builder withBasicAuth(String username, String password) {
			okHttpClientBuilder.addInterceptor(new BasicAuthInterceptor(username, password));
			return this;
		}

		public Builder withConverterRegistryConfig(ConverterRegistryConfigurer converterRegistryConsumer) {
			converterRegistryConsumer.accept(converterRegistry);
			return this;
		}

		public OkRestClient build() {

			Preconditions.checkState(buildCalled == false, "OkRestClient.Builder.build() may only be called once");
			buildCalled = true;
			OkHttpClient c = null;
			if (directlySpecifiedClient != null) {
				// use the pre-configured OkHttpClient since it was specified
				c = directlySpecifiedClient;
			} else {
				// build the OkHttpClient
				c = okHttpClientBuilder.build();
			}

			OkRestClient restClient = new OkRestClient();
			restClient.okHttpClient = c;
			restClient.registry = converterRegistry;
			restClient.getConverterRegistry().markImmutable();

			okHttpClientBuilder = null;
			converterRegistry = null;
			directlySpecifiedClient = null;

			Preconditions.checkState(restClient.okHttpClient != null);
			Preconditions.checkState(restClient.registry != null);

			long count = constructorCount.incrementAndGet();
			if (count>10 && count%10==0) {
				Logger logger = LoggerFactory.getLogger(OkRestClient.Builder.class);
				
				logger.info("potential implementation mistake: {} OkRestClient instances created",count);
				logger.debug("diagnostic stack trace",new DiagnosticStackTrace());
				
			}
			
			return restClient;

		}
	}
}
