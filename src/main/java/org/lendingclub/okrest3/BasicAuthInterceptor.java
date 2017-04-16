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
package org.lendingclub.okrest3;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

	
	private String username;
	private String password;
	
	public BasicAuthInterceptor(String username, String password) {
		this.username = username;
		this.password = password;
	}
	@Override
	public Response intercept(Chain chain) throws IOException {
		
		return chain.proceed(chain.request().newBuilder().addHeader("Authorization", Credentials.basic(username, password)).build());
	
	}

}
