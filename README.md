OkRest
======

[![Circle CI](https://circleci.com/gh/if6was9/okrest.svg?style=svg)](https://circleci.com/gh/if6was9/okrest) 
[![Download](https://api.bintray.com/packages/robschoening/io-macgyver/okrest/images/download.svg) ](https://bintray.com/robschoening/io-macgyver/okrest/_latestVersion)

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
String body = new OkRestClient()
    .uri("https://api.example.com")
    .path("/api/users/123")
    .get().execute(String.class);
```

And if we want to parse it through Jackson's fluent tree API:

```java
String name = new OkRestClient()
    .uri("https://api.example.com")
    .path("/api/users/123")
    .get().execute(JsonNode.class)
    .path("name").asText();
```
