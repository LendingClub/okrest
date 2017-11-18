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

import com.google.common.base.Optional;
import okhttp3.MediaType;
import okhttp3.Response;

public abstract class ResponseBodyConverter {

	public abstract boolean supports(Class<? extends Object> t, Optional<MediaType> mediaType);
	
	public abstract <T> T convert(Response r, Class<? extends T> t) throws IOException;
	
}
