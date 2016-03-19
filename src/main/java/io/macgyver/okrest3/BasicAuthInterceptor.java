package io.macgyver.okrest3;

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
