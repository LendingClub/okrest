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

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.lendingclub.okrest3.BasicAuthInterceptor;
import org.lendingclub.okrest3.OkRestClient;

import okhttp3.Credentials;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class BasicAuthInterceptorTest {

	@Rule
	public MockWebServer mockServer = new MockWebServer();

	@Test
	public void testIt() throws InterruptedException {

		mockServer.enqueue(new MockResponse().setBody("bar"));
		new OkRestClient.Builder()
				.withOkHttpClientConfig(it -> it.addInterceptor(new BasicAuthInterceptor("myuser", "mypass"))).build().uri(mockServer.url("/foo").toString()).get().execute();
		

		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getHeader("Authorization")).isEqualTo(Credentials.basic("myuser", "mypass"));
	}
}
