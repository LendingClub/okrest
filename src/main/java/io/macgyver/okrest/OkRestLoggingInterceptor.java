package io.macgyver.okrest;

import io.macgyver.okrest.compat.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import okio.Buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class OkRestLoggingInterceptor implements Interceptor {

	boolean logBody = true;
	Logger logger;

	public OkRestLoggingInterceptor() {
		logger = LoggerFactory.getLogger(OkRestLoggingInterceptor.class);
	}

	

	public OkRestLoggingInterceptor withLogger(String s) {
		return withLogger(LoggerFactory.getLogger(s));
	}
	public OkRestLoggingInterceptor withLogger(Class<?> c) {
		return withLogger(LoggerFactory.getLogger(c));
	}
	public OkRestLoggingInterceptor withLogger(Logger logger) {
		this.logger = logger;
		return this;
	}
	public OkRestLoggingInterceptor withBodyLogging(boolean b) {
		logBody = b;
		return this;
	}
	
	@Override
	public Response intercept(Chain chain) throws IOException {

		Request request = chain.request();

		if (logger.isDebugEnabled()) {
			StringWriter sw = new StringWriter();

			sw.write("\n");
			sw.write(request.toString());
			sw.write("\n");
			sw.write(request.headers().toString());

			RequestBody b = request.body();
			boolean hasBody = b==null;
			String contentLength = request.header("Content-length");
			hasBody = hasBody && (contentLength!=null && !contentLength.equals("0"));
			
			if (logBody) {
				

				String body = "NONE";
				
				if (!hasBody) {
					sw.write("\nBody: NONE");
				} else {
					Buffer buffer = new Buffer();
					b.writeTo(buffer);
					if (b.contentLength() > 0) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						HexDump.dump(buffer.readByteArray(), 0, baos, 0);
						sw.write(new String(baos.toByteArray()));
					}
				}
			} else {
				if (hasBody) {
					sw.write("\nBody: NONE");
				} else {
					sw.write("\nBody: ...");
				}
			}
			
			String x = sw.toString();
			x = x.replace("\n", "\n<<< ");
			logger.debug("request\n{}\n", x);

		}

		Response response = chain.proceed(request);

		Response.Builder copy = response.newBuilder();

		if (logger.isDebugEnabled()) {

			StringWriter sw = new StringWriter();
			sw.write("\n");
			sw.write(response.toString());
			sw.write("\n");
			sw.write(response.headers().toString());

			if (logBody) {
				ResponseBody body = response.body();
				byte[] bytes = body.bytes();
				if (bytes != null && bytes.length > 0) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					if (bytes.length > 0) {
						HexDump.dump(bytes, 0, baos, 0);
					}
					String bodyDump = "\n" + new String(baos.toByteArray());
					sw.write(bodyDump);

					response = copy.body(ResponseBody.create(null, bytes)).build();
					
				} else {
					sw.write("\nBody: NONE\n");
				}
			}
			else {
				sw.write("\nBody: ...\n");
			}
			String x = sw.toString().replaceAll("\n", "\n>>> ");
			if (x.endsWith(">>> ")) {
				x = x.substring(0, x.length()-4);
			}
			logger.debug("response\n{}\n", x);

		}

		return response;
	}
}
