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
package io.macgyver.okrest3.converter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import okhttp3.Response;

import io.macgyver.okrest3.OkRestException;

public class DefaultResponseErrorHandler extends ResponseErrorHandler {

	Logger logger = LoggerFactory.getLogger(DefaultResponseErrorHandler.class);

	@Override
	public <T> T handleError(Response response, Class<? extends T> clazz)
			throws OkRestException {

		OkRestException x = OkRestException.fromResponse(response);
		try {
			String s = CharStreams.toString(response.body().charStream());
			x.setErrorResponseBody(s);

		} catch (Exception ignore) {
			// ignore this
		}
		throw x;
	}

}
