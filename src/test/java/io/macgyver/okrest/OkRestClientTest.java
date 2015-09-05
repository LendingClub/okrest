package io.macgyver.okrest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import okio.Buffer;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class OkRestClientTest {


	public MockWebServer mockServer = new MockWebServer();

	OkRestTarget target;

	@BeforeClass
	public static void bridgeLogger() {
		if (!SLF4JBridgeHandler.isInstalled()) {
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();
		}
	}

	@Before
	public void setup() {

		target = new OkRestClient().uri(mockServer.getUrl("/"));
		target.getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

	}

	@Test
	public void testInstances() {
		OkRestClient c = new OkRestClient();
		assertThat(c.getOkHttpClient()).isNotNull().isSameAs(
				c.getOkHttpClient());

		OkRestClient c2 = new OkRestClient();
		assertThat(c2.getOkHttpClient()).isNotSameAs(c.getOkHttpClient());

		assertThat(c.getConverterRegistry()).isNotSameAs(
				c2.getConverterRegistry());
	}

	@Test
	public void testAddHeader() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("hello"));

		String s = target.path("/test").header("X-foo", "bar")
				.addHeader("X-foo", "baz").get().execute(String.class);

		assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getHeaders().values("X-foo")).contains("bar", "baz");
	}

	@Test
	public void testFirstClassHeaders() throws IOException,
			InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("hello"));

		String s = target.path("/test").accept("foo/bar")
				.contentType("foo/baz").get().execute(String.class);

		assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getHeader("accept")).isEqualTo("foo/bar");
		assertThat(rr.getHeader("content-type")).isEqualTo("foo/baz");
	}

	@Test
	public void testAddHeaderThenHeader() throws IOException,
			InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("hello"));

		String s = target.path("/test").header("X-foo", "bar")
				.addHeader("X-foo", "baz").header("X-foo", "oof").get()
				.execute(String.class);

		assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getHeaders().values("X-foo")).contains("oof").hasSize(1);
	}

	@Test
	public void testCreate() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.path("abc");

		assertThat(r).isNotSameAs(r2);
		assertThat(r.getOkRestClient()).isSameAs(r2.getOkRestClient());
		assertThat(r.getOkHttpClient()).isSameAs(r2.getOkHttpClient());

		assertThat(r2.getUrl().toString()).isEqualTo(
				"https://www.google.com/abc");
		assertThat(r.getUrl().toString()).isEqualTo("https://www.google.com");

	}

	@Test
	public void testQueryParam() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.path("abc").queryParameter("a", "1");

		assertThat(r).isNotSameAs(r2);
		assertThat(r.getOkRestClient()).isSameAs(r2.getOkRestClient());
		assertThat(r.getOkHttpClient()).isSameAs(r2.getOkHttpClient());

		assertThat(r2.getUrl().toString()).isEqualTo(
				"https://www.google.com/abc?a=1");
		assertThat(r.getUrl().toString()).isEqualTo("https://www.google.com");

	}

	@Test
	public void testGuavaMultiMap() {
		Multimap<String, String> x = ArrayListMultimap.create();
		x.put("a", "1");
		x.put("a", "2");
		assertThat(x.get("a")).contains("1", "2");
	}

	@Test
	public void testHeaders() {
		OkRestClient c = new OkRestClient();
		OkRestTarget r = c.uri("https://www.google.com");

		assertThat(r.getOkUriBuilder().build().toString()).isEqualTo(
				"https://www.google.com");

		OkRestTarget r2 = r.header("X-foo", "bar");
		assertThat(r.getHeaders().get("X-foo")).isNull();

		assertThat(r2.getHeaders().get("x-Foo")).contains("bar");

	}

	@Test
	public void testIt2() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestTarget c = new OkRestClient().uri(
				mockServer.getUrl("/test").toString()).header("x-foo", "bar");

		Response r = c.get().execute().response();

		assertThat(r).isNotNull();

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getPath()).isEqualTo("/test");
		assertThat(rr.getHeader("X-foo")).isEqualTo("bar");

	}

	@Test
	public void testResponse() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("hello"));

		String s = target.path("/test").get().execute(String.class);

		assertThat(s).isEqualTo("hello");

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getPath()).isEqualTo("/test");

	}

	@Test
	public void testResponseWithError() throws IOException,
			InterruptedException {

		try {
			mockServer.enqueue(new MockResponse().setResponseCode(400).setBody(
					"xxx"));

			String s = target.path("/test").get().execute(String.class);

			assertThat(s).isEqualTo("hello");

			Assert.fail();

		} catch (OkRestException e) {
			assertThat(e).hasMessageContaining("400");
		}

	}

	@Test
	public void testGET() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target.path("hello/world").get()
				.header("foo", "bar").execute();

		assertThat(r).isNotNull();

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("GET");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testDELETE() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target.path("hello/world").delete()
				.header("foo", "bar").execute();
		assertThat(r).isNotNull();
		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("DELETE");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testPOST() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRestResponse r = target
				.path("hello/world")
				.post(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		assertThat(r).isNotNull();
		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("POST");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");

		assertThat(rr.getBody().readUtf8()).isEqualTo("{\"a\":1}");

	}

	@Test
	public void testBinaryPost() throws IOException, InterruptedException {
		byte[] testData = new byte[256];
		for (int i = 0; i < testData.length; i++) {
			testData[i] = (byte) i;
		}

		mockServer.enqueue(new MockResponse().setBody(
				new Buffer().write(testData)).addHeader("content-type",
				"application/octet-bar"));

		Buffer requestBuffer = new Buffer();
		requestBuffer.write(testData);

		OkRestResponse r = target
				.path("hello/world")
				.post(RequestBody.create(
						MediaType.parse("application/octet-foo"), testData))
				.header("foo", "bar").execute();

		assertThat(r).isNotNull();
		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("POST");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");
		assertThat(rr.getHeader("Content-type")).contains("octet-foo");

		assertThat(r.response().header("Content-type")).contains(
				"application/octet-bar");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		rr.getBody().copyTo(baos);

		byte[] resultData = baos.toByteArray();

		Assertions.assertThat(resultData).containsSequence(testData);

	}

	@Test
	public void testDefaultContentType() throws IOException,
			InterruptedException {
		mockServer.enqueue(new MockResponse().setBody(""));
		target.path("test").post(RequestBody.create(null, "hello")).execute();

		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getHeader("Content-type")).isNull();
	}

	@Test
	public void testPUT() throws IOException, InterruptedException {

		RecordedRequest rr;

		OkRestTarget tt = target;
		mockServer.enqueue(new MockResponse().setBody("{}"));
		OkRestResponse r = target.path("hello/world")
				.put(RequestBody.create(MediaType.parse("text/plain"), "test"))
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();

		assertThat(r).isNotNull();
		assertThat(tt).isNotNull();
		assertThat(rr.getHeader("Content-type")).contains("text/plain");
		assertThat(rr.getPath()).isEqualTo("/hello/world");

		mockServer.enqueue(new MockResponse().setBody("{}"));

		r = target
				.path("hello/world")
				.put(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("PUT");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");
		assertThat(rr.getBody().readUtf8()).isEqualTo("{\"a\":1}");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(new ObjectMapper().createObjectNode())
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();
		assertThat(rr.getHeader("Content-type")).contains("application/json");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(new ObjectMapper().createObjectNode())
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();
		assertThat(rr.getHeader("Content-type")).contains("application/json");

		mockServer.enqueue(new MockResponse().setBody("{}"));
		r = target.path("hello/world")
				.put(RequestBody.create(MediaType.parse("text/plain"), "test"))
				.header("foo", "bar").execute();
		rr = mockServer.takeRequest();

		assertThat(rr.getHeader("Content-type")).contains("text/plain");
	}

	@Test
	public void testHEAD() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse());

		OkRestResponse r = target
				.path("hello/world")
				.head(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		assertThat(r).isNotNull();
		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("HEAD");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");

	}

	@Test
	public void testPATCH() throws InterruptedException {
		mockServer.enqueue(new MockResponse());

		OkRestResponse r = target
				.path("hello/world")
				.patch(RequestBody.create(MediaType.parse("application/json"),
						"{\"a\":1}")).header("foo", "bar").execute();

		assertThat(r).isNotNull();
		RecordedRequest rr = mockServer.takeRequest();
		assertThat(rr.getMethod()).isEqualTo("PATCH");
		assertThat(rr.getPath()).isEqualTo("/hello/world");
		assertThat(rr.getHeader("FOO")).isEqualTo("bar");

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

		assertThat(rr.getHeader("Content-Type")).contains("foo/bar"); // Really???

	}

	@Test
	public void testJacksonResponse() throws InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{\"name\":\"Rob\"}"));
		JsonNode n = target.path("/").get().execute(JsonNode.class);
		assertThat(n.path("name").asText()).isEqualTo("Rob");

		mockServer.enqueue(new MockResponse().setBody("[{\"name\":\"Rob\"}]"));
		n = target.path("/").get().execute(JsonNode.class);
		assertThat(n.get(0).path("name").asText()).isEqualTo("Rob");

		try {
			mockServer.enqueue(new MockResponse().setBody("<test/>"));
			n = target.path("/").get().execute(JsonNode.class);

			Assert.fail();
		} catch (Exception e) {
			assertThat(e).isExactlyInstanceOf(OkRestException.class);
		}
	}

	@Test
	public void testInputStreamResponse() throws InterruptedException,
			IOException {
		byte[] x = "hello".getBytes();
		@SuppressWarnings("resource")
		Buffer b = new Buffer().write(x);

		mockServer.enqueue(new MockResponse().setBody(b));
		InputStream x1 = target.path("/").get().execute(InputStream.class);
		byte[] bx1 = ByteStreams.toByteArray(x1);

		assertThat(bx1).isEqualTo(x);

	}

	@Test
	public void testCharacterStreamResponse() throws InterruptedException,
			IOException {
		String x = "hello";
		mockServer.enqueue(new MockResponse().setBody(x));
		Reader x1 = target.path("/").get().execute(Reader.class);
		String bx1 = CharStreams.toString(x1);

		assertThat(bx1).isEqualTo(x);
	}

	@SuppressWarnings("resource")
	@Test
	public void testByteArrayResponse() throws InterruptedException,
			IOException {
		byte[] x = new byte[256];
		for (int i = 0; i < x.length; i++) {
			x[i] = (byte) i;
		}

		mockServer.enqueue(new MockResponse().setBody(new Buffer().write(x)));
		byte[] x1 = target.path("/").get().execute(byte[].class);
		Assert.assertArrayEquals(x, x1);
		assertThat(x1).isEqualTo(x);
	}

	@Test
	public void testExceptionBody() throws IOException, InterruptedException {

		String responseBody = "{\"message\":\"all screwed up\"}";
		mockServer.enqueue(new MockResponse().setBody(responseBody
				).setResponseCode(500));

		try {
			ObjectNode s = target.path("/test").header("X-foo", "bar")
					.addHeader("X-foo", "baz").header("X-foo", "oof").get()
					.execute(ObjectNode.class);
			Assert.fail();
		} catch (OkRestException e) {
			Assertions.assertThat(e.getStatusCode()).isEqualTo(500);
			Assertions.assertThat(e.getErrorResponseBody()).isEqualTo(responseBody);
		}

	}
}
