package io.macgyver.okrest3;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

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
