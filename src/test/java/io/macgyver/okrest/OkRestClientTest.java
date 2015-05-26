package io.macgyver.okrest;

import static org.assertj.core.api.Assertions.assertThat;
import io.macgyver.okrest.compat.OkUriBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;

public class OkRestClientTest {

	@Rule
	public MockWebServerRule mockServer = new MockWebServerRule();

	OkRestTarget target;

	@Before
	public void setup() {

		target = new OkRestClient()
				.uri(mockServer.getUrl("/"));
	}

	@Test
	public void testInstances() {
		OkRestClient c = new OkRestClient();
		Assertions.assertThat(c.getOkHttpClient()).isNotNull()
				.isSameAs(c.getOkHttpClient());

		OkRestClient c2 = new OkRestClient();
		Assertions.assertThat(c2.getOkHttpClient()).isNotSameAs(
				c.getOkHttpClient());
	}

	@Test
	public void testAddHeader() throws IOException, InterruptedException{
		mockServer.enqueue(new MockResponse().setBody("hello"));
		
		String s = target.path("/test").header("X-foo","bar").addHeader("X-foo", "baz").get().execute(String.class);

		Assertions.assertThat(s).isEqualTo("hello");
	
		
		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getHeaders("X-foo")).contains("bar","baz");
	}
	@Test
	public void testAddHeaderThenHeader() throws IOException, InterruptedException{
		mockServer.enqueue(new MockResponse().setBody("hello"));
		
		String s = target.path("/test").header("X-foo","bar").addHeader("X-foo", "baz").header("X-foo", "oof").get().execute(String.class);

		Assertions.assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getHeaders("X-foo")).contains("oof").hasSize(1);
	}
	@Test
	public void testCreate() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.path("abc");

		Assertions.assertThat(r).isNotSameAs(r2);
		Assertions.assertThat(r.getOkRestClient()).isSameAs(
				r2.getOkRestClient());
		Assertions.assertThat(r.getOkHttpClient()).isSameAs(
				r2.getOkHttpClient());

		Assertions.assertThat(r2.getUrl().toString()).isEqualTo(
				"https://www.google.com/abc");
		Assertions.assertThat(r.getUrl().toString()).isEqualTo(
				"https://www.google.com");

	}

	@Test
	public void testQueryParam() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.path("abc").queryParameter("a", "1");

		Assertions.assertThat(r).isNotSameAs(r2);
		Assertions.assertThat(r.getOkRestClient()).isSameAs(
				r2.getOkRestClient());
		Assertions.assertThat(r.getOkHttpClient()).isSameAs(
				r2.getOkHttpClient());

		Assertions.assertThat(r2.getUrl().toString()).isEqualTo(
				"https://www.google.com/abc?a=1");
		Assertions.assertThat(r.getUrl().toString()).isEqualTo(
				"https://www.google.com");

	}

	@Test
	public void testGuavaMultiMap() {
		Multimap<String, String> x = ArrayListMultimap.create();
		x.put("a", "1");
		x.put("a", "2");
		Assertions.assertThat(x.get("a")).contains("1", "2");
	}

	@Test
	public void testHeaders() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.header("X-foo", "bar");
		Assertions.assertThat(r.getHeaders().get("X-foo")).isNull();

		Assertions.assertThat(r2.getHeaders().get("x-Foo")).contains("bar");

	}

	@Test
	public void testIt2() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestTarget c = new OkRestClient().uri(
				mockServer.getUrl("/test").toString()).header("x-foo", "bar");

		Response r = c.get().execute().response();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getPath()).isEqualTo("/test");
		Assertions.assertThat(rr.getHeader("X-foo")).isEqualTo("bar");

	}

	@Test
	public void testResponse() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("hello"));

		String s = target.path("/test").get().execute(String.class);

		Assertions.assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getPath()).isEqualTo("/test");

	}

	@Test
	public void testResponseWithError() throws IOException,
			InterruptedException {

		try {
			mockServer.enqueue(new MockResponse().setResponseCode(400).setBody(
					"xxx"));

			String s = target.path("/test").get().execute(String.class);

			Assertions.assertThat(s).isEqualTo("hello");

			Assert.fail();

		} catch (OkRestException e) {
			Assertions.assertThat(e).hasMessageContaining("400");
		}

	}

	@Test
	public void testGET() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target.path("hello/world").get()
				.header("foo", "bar").execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("GET");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testDELETE() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target.path("hello/world").delete()
				.header("foo", "bar").execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("DELETE");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testPOST() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target
				.path("hello/world")
				.post(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("POST");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");

		Assertions.assertThat(rr.getUtf8Body()).isEqualTo("{\"a\":1}");

	}

	@Test
	public void testDefaultContentType() throws IOException,
			InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));
		target.path("test").post(RequestBody.create(null, "hello")).execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getHeader("Content-type")).isNull();
	}

	@Test
	public void testPUT() throws IOException, InterruptedException {
		OkRestResponse r;
		RecordedRequest rr;

		OkRestTarget tt = target;
		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(RequestBody.create(MediaType.parse("text/plain"), "test"))
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getHeader("Content-type")).contains(
				"text/plain");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");

		mockServer.enqueue(new MockResponse().setBody("{}"));

		r = target
				.path("hello/world")
				.put(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("PUT");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");
		Assertions.assertThat(rr.getUtf8Body()).isEqualTo("{\"a\":1}");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(new ObjectMapper().createObjectNode())
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getHeader("Content-type")).contains(
				"application/json");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(new ObjectMapper().createObjectNode())
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getHeader("Content-type")).contains(
				"application/json");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(RequestBody.create(MediaType.parse("text/plain"), "test"))
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getHeader("Content-type")).contains(
				"text/plain");
	}

	@Test
	public void testHEAD() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse());

		OkRestResponse r = target
				.path("hello/world")
				.head(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("HEAD");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testPATCH() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse());

		OkRestResponse r = target
				.path("hello/world")
				.patch(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("PATCH");
		Assertions.assertThat(rr.getPath()).isEqualTo("/hello/world");
		Assertions.assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testConentTypeBehavior() throws InterruptedException,
			IOException {

		// This seems like a bug

		mockServer.enqueue(new MockResponse());

		OkHttpClient c = new OkHttpClient();

		Request r = new Request.Builder().url(mockServer.getUrl("/"))
				.header("Content-type", "text/xml")
				.post(RequestBody.create(MediaType.parse("foo/bar"), ""))
				.removeHeader("Content-type")
				.header("Content-Type", "text/plain").build();

		c.newCall(r).execute();

		RecordedRequest rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getHeader("Content-Type")).contains("foo/bar"); // Really???

	}
	
	@Test
	public void testJacksonResponse()  throws InterruptedException, IOException {
		mockServer.enqueue(new MockResponse().setBody("{\"name\":\"Rob\"}"));
		JsonNode n = target.path("/").get().execute(JsonNode.class);
		Assertions.assertThat(n.path("name").asText()).isEqualTo("Rob");
		
		
		mockServer.enqueue(new MockResponse().setBody("[{\"name\":\"Rob\"}]"));
		n = target.path("/").get().execute(JsonNode.class);
		Assertions.assertThat(n.get(0).path("name").asText()).isEqualTo("Rob");
		
		
		try {
		mockServer.enqueue(new MockResponse().setBody("<test/>"));
		n = target.path("/").get().execute(JsonNode.class);
		
		Assert.fail();
		}
		catch (IOException e) {
			Assertions.assertThat(e).isExactlyInstanceOf(JsonParseException.class);
		}
	}
	
	
	@Test
	public void testInputStreamResponse()  throws InterruptedException, IOException {
		byte [] x = {0,1,2};
		mockServer.enqueue(new MockResponse().setBody(x));
		InputStream x1 = target.path("/").get().execute(InputStream.class);
		byte [] bx1 = ByteStreams.toByteArray(x1);
		
		Assertions.assertThat(bx1).isEqualTo(x);
	}
	@Test
	public void testCharacterStreamResponse()  throws InterruptedException, IOException {
		String x = "hello";
		mockServer.enqueue(new MockResponse().setBody(x));
		Reader x1 = target.path("/").get().execute(Reader.class);
		String bx1 = CharStreams.toString(x1);
		
		Assertions.assertThat(bx1).isEqualTo(x);
	}
	
	@Test
	public void testByteArrayResponse()  throws InterruptedException, IOException {
		byte [] x = {0,1,2};
		mockServer.enqueue(new MockResponse().setBody(x));
		byte [] x1 = target.path("/").get().execute(byte[].class);
		Assertions.assertThat(x1).isEqualTo(x);
	}
}
