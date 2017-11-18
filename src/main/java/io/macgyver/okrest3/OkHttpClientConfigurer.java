package io.macgyver.okrest3;

import java.util.function.Consumer;

import okhttp3.OkHttpClient;

public interface OkHttpClientConfigurer extends Consumer<OkHttpClient.Builder>{

}
