OkRest
======

[![CircleCI](https://circleci.com/gh/LendingClub/okrest.svg?style=svg)](https://circleci.com/gh/LendingClub/okrest)
[![Download](https://img.shields.io/maven-central/v/io.macgyver.okrest3/okrest.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.macgyver.okrest3%22)
[ ![Download](https://api.bintray.com/packages/robschoening/io-macgyver/okrest3/images/download.svg) ](https://bintray.com/robschoening/io-macgyver/okrest3/_latestVersion)

OkRest is a fluent REST client that is built on Square's excellent [OkHttp](https://square.github.io/okhttp/) client.

OkRest's API is similar in construction JAX-RS, but without all of the JAX-RS baggage.



Examples
--------

Assuming that a fictitious service with a GET request for ```/api/users/123``` returns:
```json
{
  "id" : 123,
  "name" : "Rob"
}
```

This will get the body as a String:

```java
String body = new OkRestClient.Builder().build()
    .uri("https://api.example.com")
    .path("/api/users/123")
    .get().execute(String.class);
```

And if we want to parse it through Jackson's fluent tree API:

```java
String name = new OkRestClient.Builder().build()
    .uri("https://api.example.com")
    .path("/api/users/123")
    .get().execute(JsonNode.class)
    .path("name").asText();
```

Configuration
-------------

OkRest will automatically create an OkHttpClient instance to be used internally:

```java
OkRestClient client = new OkRestClient.Builder().build();
```

To configure the OkHttpClient instance during configuration, there is a handy method ```withOkHttpClientConfig``` that accepts a lambda
which will be executed during construction of the OkHttpClient.  Beautiful!

```java
OkRestClient client = new OkRestClient.Builder()
                            .withOkHttpClientConfig(cfg -> cfg.addInterceptor(myInterceptor))
                            .build();
```

If you already have an OkHttpClient that you'd like to use, you can tell OkRest to use that:

```java
OkRestClient client = new OkRestClient.Builder().withOkHttpClient(okHttpClient).build();
```

Similarly, if you already have an OkHttpClient.Builder instance, you can pass that in.  This would be needed if you have an immutable OkHttpClient instance and need to copy/modify the config:

```java
OkRestClient client = new OkRestClient.Builder().withOkHttpClientBuilder(okHttpClientBuilder).build();
```

Remember: OkHttpClient and OkRestClient instances should be shared where possible. 

Change Log
-----------
### 3.1.0

* Properly close all fully consumed streams
* Upgrade to OkHttp 3.4.1
* Remove LoggingInterceptor -- the one from OkHttp is fine

### 3.0.4

### 3.0.3

* Added ability to set query parameters from a map or variable argument list

### 3.0.2

OkRest 3 is a major release that tracks OkHttp 3.x.  

Since OkHttp changed its APIs in 3.x, we have done the same in OkRest 3.x.  Applications should be able to upgrade from the 2.x API to the 3.x API mechanically and with minimal risk.

Like OkHttp, this release contains breaking changes.  In order to support a smooth transition, the package naming has been changed from ```io.macgyver.okrest``` to ```io.macgyver.okrest3```.  The maven group has also been changed from ```io.macgyver.okrest``` to ```io.macgyver.okrest3```.

This should allow applications to use OkRest 2.x and 3.x at the same time.  For an explanation of this strategy, see Jake Wharton's post, 
[Java Interoperability Policy for Major Version Updates](http://jakewharton.com/java-interoperability-policy-for-major-version-updates/).

The most significant changes are that ```OkRestClient``` instances are now immutable and must instantiated by a builder.

* OkRest 3 is a major release that tracks OkHttp 3.x
* Upgrade to OkHttp 3.2.x
* Clients are now immutable, just like OkHttpClient
