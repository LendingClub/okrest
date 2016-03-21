package io.macgyver.okrest3;

import java.io.IOException;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Maps;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class OkRestTargetTest {

	@Rule
	public MockWebServer mockServer = new MockWebServer();
	
	
	@Test
	public void testIt() throws IOException, InterruptedException {
		
		mockServer.enqueue(new MockResponse().setBody("bar"));
		new OkRestClient.Builder().withInterceptor(new LoggingInterceptor()).build().uri(mockServer.url("/foo").toString()).queryParam("foo","bar","baz","1").get().execute().getBody(String.class);
		
		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getRequestLine()).isEqualTo("GET /foo?foo=bar&baz=1 HTTP/1.1");
		
		mockServer.enqueue(new MockResponse().setBody("bar"));
		new OkRestClient.Builder().withInterceptor(new LoggingInterceptor()).build().uri(mockServer.url("/foo").toString()).queryParameMultiValue("a","b","c").queryParam("foo","bar","baz","1").get().execute().getBody(String.class);
		
		 rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getRequestLine()).isEqualTo("GET /foo?a=b&a=c&foo=bar&baz=1 HTTP/1.1");
		
		
		
		mockServer.enqueue(new MockResponse().setBody("bar"));
		Map<String,Integer> m = Maps.newLinkedHashMap();
		m.put("a", 1);
		m.put("b",2);
		m.put("c",null);
		new OkRestClient.Builder().withInterceptor(new LoggingInterceptor()).build().uri(mockServer.url("/foo").toString()).queryParam(m).get().execute().getBody(String.class);
		
		 rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getRequestLine()).isEqualTo("GET /foo?a=1&b=2&c= HTTP/1.1");
		
	}
}
