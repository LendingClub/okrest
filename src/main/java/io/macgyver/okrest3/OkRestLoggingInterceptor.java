package io.macgyver.okrest3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import okio.Buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import io.macgyver.okrest3.compat.HexDump;

public class OkRestLoggingInterceptor implements Interceptor {

	public long LOG_BODY_MAXLEN = 1024 * 100;

	boolean logBodySetting = true;
	Logger logger;

	long bodySizeMaxBytes = LOG_BODY_MAXLEN;

	public static final String REQUEST_LOG_PREFIX = "<<< ";
	public static final String RESPONSE_LOG_PREFIX = ">>> ";

	public static final String BODY_LENGTH_UNKNOWN_OR_TOO_LONG = "--- body length unknown or too long ---";

	public static class ResponsePlugLog {
		Response response;
		String log;
	}

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
		logBodySetting = b;
		return this;
	}

	protected boolean isLogRequestBody(Request request) throws IOException {

		RequestBody body = request.body();
		if (body == null) {
			return false;
		}
		long bodyLength = body.contentLength();

		if (bodyLength < 0) {
			// unknown body length...don't log
			return false;
		} else if (bodyLength > LOG_BODY_MAXLEN) {
			return false;
		}

		return true;

	}

	protected boolean shouldLogResponse(Response response) {
		boolean localLogBody = logBodySetting;
		long contentLength = bodySizeMaxBytes;
		try {

			contentLength = Long.parseLong(response.header("Content-length",""+bodySizeMaxBytes)
					 );
			localLogBody = logBodySetting && (contentLength < bodySizeMaxBytes);
		} catch (Exception e) {
		}
		return localLogBody;
	}

	ResponsePlugLog formatResponseLog(Chain chain, Response response)
			throws IOException {

		ResponsePlugLog responsePlugLog = new ResponsePlugLog();
		responsePlugLog.response = response;
		Response.Builder copy = response.newBuilder();
		StringWriter sw = new StringWriter();
		sw.write("\n");
		sw.write(response.toString());
		sw.write("\n");
		sw.write(response.headers().toString());

		if (shouldLogResponse(response)) {
			ResponseBody body = response.body();
			byte[] bytes = body.bytes();
			sw.write("\n");
			if (bytes != null && bytes.length > 0) {

				String x = new String(bytes);
				if (isPrintable(x)) {

					sw.write(new String(bytes));

				} else {
					ByteArrayOutputStream tmp = new ByteArrayOutputStream();
					HexDump.dump(bytes, 0, tmp, 0);
					sw.write(new String(tmp.toByteArray()));
				}

				responsePlugLog.response = copy.body(
						ResponseBody.create(null, bytes)).build();

			} else {
				sw.write("\n--- no body ---\n");
			}
		} else {
			sw.write("\n" + BODY_LENGTH_UNKNOWN_OR_TOO_LONG + "\n");
		}
		String x = sw.toString().replaceAll("\n", "\n" + RESPONSE_LOG_PREFIX);
		if (x.endsWith(RESPONSE_LOG_PREFIX)) {
			x = x.substring(0, x.length() - RESPONSE_LOG_PREFIX.length());
		}
		responsePlugLog.log = x;
		return responsePlugLog;
	}

	String formatRequestLog(Chain chain) throws IOException {
		Request request = chain.request();
		StringWriter sw = new StringWriter();

		sw.write("\n");
		sw.write(request.toString());
		sw.write("\n");
		sw.write(request.headers().toString());

		RequestBody b = request.body();
		boolean hasBody = request.body() != null
				&& request.body().contentLength() > 0;

		if (isLogRequestBody(request)) {

			if (!hasBody) {
				// no body
			} else {

				Buffer buffer = new Buffer();
				request.body().writeTo(buffer);
				byte[] data = buffer.readByteArray();

				String x = new String(data);
				if (isPrintable(x)) {
					sw.write(x);
				} else {
					ByteArrayOutputStream tmp = new ByteArrayOutputStream();
					HexDump.dump(data, 0, tmp, 0);
					sw.write(new String(tmp.toByteArray()));
				}

			}
		} else {
			if (!hasBody) {
				// no body
			} else {
				sw.write("\n--- body too large or size unknown ---");
			}
		}

		String x = sw.toString();
		x = x.replace("\n", "\n" + REQUEST_LOG_PREFIX);
		if (x.endsWith(REQUEST_LOG_PREFIX)) {
			x = x.substring(0, x.length() - REQUEST_LOG_PREFIX.length());
		}

		return x;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {

		Request request = chain.request();

		if (logger.isDebugEnabled()) {
			logger.debug("request\n{}\n", formatRequestLog(chain));

		}

		Response response = chain.proceed(request);

		if (logger.isDebugEnabled()) {
			ResponsePlugLog log = formatResponseLog(chain, response);
			logger.debug("Response\n{}\n", log.log);
			response = log.response;
		}

		return response;
	}

	public static boolean isPrintable(String s) {
		if (s == null) {
			return false;
		} else {
			for (int i = 0; i < s.length(); i++) {
				if (!isPrintable(s.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isPrintable(char ch) {
		return Character.isWhitespace(ch) || (ch >= 32 && ch < 127);
	}

	public long getBodySizeMaxBytes() {
		return bodySizeMaxBytes;
	}

	public void setBodySizeMaxBytes(long bodySizeMaxBytes) {
		this.bodySizeMaxBytes = bodySizeMaxBytes;
	}

}
