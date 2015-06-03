package io.macgyver.okrest;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OkRestExceptionTest {

	
	@Test
	public void testIt() {
		OkRestException e = new OkRestException(500);
		Assertions.assertThat(e).hasMessageContaining("statusCode=500");
		Assertions.assertThat(e.getStatusCode()).isEqualTo(500);
	}
	
	@Test
	public void testExceptionWithMessage() {
		OkRestException e = new OkRestException(500, "some problem");
		Assertions.assertThat(e).hasMessageContaining("statusCode=500: some problem");
		Assertions.assertThat(e.getStatusCode()).isEqualTo(500);
		Assertions.assertThat(e.getCause()).isNull();
	}
	
	@Test
	public void testWrapper() {
		IOException inside = new IOException("uhoh");
		OkRestException e = new OkRestWrapperException(inside);
		Assertions.assertThat(e).hasMessageContaining("statusCode=400: java.io.IOException: uhoh");
		Assertions.assertThat(e.getStatusCode()).isEqualTo(400);
		Assertions.assertThat(e.getCause()).isSameAs(inside);
	}
}
