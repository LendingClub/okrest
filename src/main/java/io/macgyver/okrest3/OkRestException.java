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
package io.macgyver.okrest3;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkRestException extends RuntimeException {

	public static final int EXCEPTION_STATUS_CODE = 400;
	private static final long serialVersionUID = 1L;
	int statusCode;
	String errorBody = null;

	protected OkRestException(Throwable t) {
		super(formatMessage(EXCEPTION_STATUS_CODE, t.toString()), t);
		this.statusCode = EXCEPTION_STATUS_CODE;
	}

	public OkRestException(int statusCode) {
		super(formatMessage(statusCode, null));
		this.statusCode = statusCode;
	}

	public OkRestException(int statusCode, String message) {
		super(formatMessage(statusCode, message));
		this.statusCode = statusCode;
	}

	public static OkRestException fromResponse(Response r) {
		OkRestException exception = new OkRestException(r.code());

		return exception;
	}


	public void setErrorResponseBody(String body) {
		this.errorBody = body;
	}
	public String getErrorResponseBody() {
		return errorBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public static String formatMessage(int statusCode, String message) {
		String s = "statusCode=" + statusCode;
		if (message != null) {
			s = s + ": " + message;
		}
		return s;
	}


}
