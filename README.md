OkRest
======

[![Circle CI](https://circleci.com/gh/LendingClub/okrest.svg?style=svg)](https://circleci.com/gh/LendingClub/okrest) 
[![Download](https://img.shields.io/maven-central/v/io.macgyver.okrest/okrest.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.macgyver.okrest%22)

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


Change Log
-----------


### 3.0.0 

OkRest 3 is a major release that tracks OkHttp 3.x.  

Since OkHttp changed its APIs in 3.x, we have done the same in OkRest 3.x.  Applications should be able to upgrade from the 2.x API to the 3.x API mechanically and with minimal risk.

Like OkHttp, this release contains breaking changes.  In order to support a smooth transition, the package naming has been changed from ```io.macgyver.okrest``` to ```io.macgyver.okrest3```.  The maven group has not 
changed, but the artifact name has changed from ```okhttp``` to ```okhttp3```.

This should allow applications to use OkRest 2.x and 3.x at the same time.  For an explanation of this strategy, see Jake Wharton's post, 
[Java Interoperability Policy for Major Version Updates](http://jakewharton.com/java-interoperability-policy-for-major-version-updates/).

The most significant changes are that ```OkRestClient``` instances are now immutable and must instantiated by a builder.

* OkRest 3 is a major release that tracks OkHttp 3.x
* Upgrade to OkHttp 3.2.x
* Clients are now immutable, just like OkHttpClient
