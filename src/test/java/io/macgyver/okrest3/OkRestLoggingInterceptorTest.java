package io.macgyver.okrest3;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.okrest3.OkRestLoggingInterceptor;

public class OkRestLoggingInterceptorTest {

	
	@Test
	public void testIsPrintable() {
		Assertions.assertThat(OkRestLoggingInterceptor.isPrintable("\r\n\t")).isTrue();
		
	}
}
