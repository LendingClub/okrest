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

import com.google.common.base.Optional;
import okhttp3.MediaType;
import okhttp3.Response;

import io.macgyver.okrest3.converter.ResponseBodyConverter;

public class OkRestResponse {

	Response response;
	OkRestTarget okRestTarget;

	public OkRestResponse(OkRestTarget target, Response r) {
		okRestTarget = target;
		this.response = r;
	}

	public Response response() {
		return response;
	}

	public <T> T getBody(Class<? extends T> clazz) throws OkRestException {

		Response response = response();

		if (response.isSuccessful()) {
			try {
				String contentType = response.header("content-type");
				MediaType mt = contentType != null ? MediaType.parse(response.header("Content-type")) : null;
				ResponseBodyConverter c = okRestTarget.findResponseConverter(clazz, Optional.fromNullable(mt));
				return c.convert(response, clazz);
			} catch (IOException e) {
				if (response!=null) {
					response.body().close();
				}
				throw new OkRestException(e);
			}
			catch (RuntimeException e) {
				if (response!=null) {
					response.body().close();
				}
				throw e;
			}
		} else {
			try {
				return okRestTarget.getOkRestClient().getConverterRegistry().findErrorHandler(clazz)
						.handleError(response, clazz);
			} finally {
				if (response != null) {
					response.body().close();
				}
			}
		}

	}
}
