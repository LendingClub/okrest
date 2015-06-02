package io.macgyver.okrest;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OkRestLoggingInterceptorTest {

	
	@Test
	public void testIsPrintable() {
		Assertions.assertThat(OkRestLoggingInterceptor.isPrintable("\r\n\t")).isTrue();
		
	}
}
