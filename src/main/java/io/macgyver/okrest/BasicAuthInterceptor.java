package io.macgyver.okrest;

import java.io.IOException;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

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