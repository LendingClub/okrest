package io.macgyver.okrest3;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import okhttp3.Headers;

public class HeadersTest {

	@Test
	public void testIt() {
		Headers h = Headers.of("X-foo","a");
		
		Assertions.assertThat(h.get("x-foo")).isEqualTo("a");
		
		
	}
}
