package io.macgyver.okrest3;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OkRestClientBuilderTest {

	
	@Test
	public void testBuilder() {
		
		new OkRestClient.Builder().withBuilder(b -> {
			Assertions.assertThat(b).isNotNull();
			
		});
		
		new OkRestClient.Builder().withOkHttpClientBuilder(b -> {
			Assertions.assertThat(b).isNotNull();
		});
	}
}
