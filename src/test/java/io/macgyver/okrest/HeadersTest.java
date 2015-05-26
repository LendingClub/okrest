package io.macgyver.okrest;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.squareup.okhttp.Headers;

public class HeadersTest {

	@Test
	public void testIt() {
		Headers h = Headers.of("X-foo","a");
		
		Assertions.assertThat(h.get("x-foo")).isEqualTo("a");
		
		
	}
}
