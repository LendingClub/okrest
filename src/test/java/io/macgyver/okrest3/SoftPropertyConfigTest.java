package io.macgyver.okrest3;

import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class SoftPropertyConfigTest {

	@Rule
	public MockWebServer mockServer = new MockWebServer();
	
	@Test
	public void testIntVal() {
		Assertions.assertThat(SoftPropertyConfig.safeIntValue(new Properties(), "foo").isPresent())
				.isFalse();
		Assertions.assertThat(SoftPropertyConfig.safeIntValue(new Properties(), "").isPresent()).isFalse();
		Assertions.assertThat(SoftPropertyConfig.safeIntValue(new Properties(), null).isPresent())
				.isFalse();
		Assertions.assertThat(SoftPropertyConfig.safeIntValue(null, "foo").isPresent()).isFalse();

		Properties p = new Properties();
		p.put("foo", "abc");

		Assertions.assertThat(SoftPropertyConfig.safeIntValue(p, "foo").isPresent()).isFalse();

		p.put("foo", "123");

		Assertions.assertThat(SoftPropertyConfig.safeIntValue(p, "foo").get()).isEqualTo(123);

		p.put("foo", " 456 ");

		Assertions.assertThat(SoftPropertyConfig.safeIntValue(p, "foo").get()).isEqualTo(456);

		p.put("foo", " -456 ");

		Assertions.assertThat(SoftPropertyConfig.safeIntValue(p, "foo").get()).isEqualTo(-456);

		p.put("foo", " 456.44 ");

		Assertions.assertThat(SoftPropertyConfig.safeIntValue(p, "foo").isPresent()).isFalse();
	}
	
	@Test
	public void testBasicAuthInterceptor() throws InterruptedException{
		
		
		Properties p = new Properties();
		p.put("username", "scott");
		p.put("password", "tiger");
	
		OkRestClient c = new OkRestClient.Builder().withOkHttpClientConfig(SoftPropertyConfig.basicAuthConfig(p)).build();
		
		c.getOkHttpClient().interceptors().forEach(interceptor -> {
			Assertions.assertThat(interceptor).isInstanceOf(BasicAuthInterceptor.class);
		});
		
		mockServer.enqueue(new MockResponse().setBody("bar"));
		String x = c.uri(mockServer.url("/foo").toString()).get().execute(String.class);
		
		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getHeader("Authorization")).isEqualTo(Credentials.basic("scott", "tiger"));
	}
}
