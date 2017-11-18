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
