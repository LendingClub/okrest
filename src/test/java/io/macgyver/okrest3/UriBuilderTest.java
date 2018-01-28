/**
 * Copyright 2017-2018 LendingClub, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.okrest3;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.okrest3.compat.OkUriBuilder;

public class UriBuilderTest {

	@Test
	public void testIt() {
		
		Assertions.assertThat(new OkUriBuilder().uri("http://www.yahoo.com").scheme("https").build().toString()).isEqualTo("https://www.yahoo.com");
		
		Assertions.assertThat(new OkUriBuilder().uri("http://www.yahoo.com").port(8443).scheme("https").queryParam("a", "1").build().toString()).isEqualTo("https://www.yahoo.com:8443?a=1");
		
		Assertions.assertThat(new OkUriBuilder().uri("http://www.yahoo.com").host("localhost").port(8443).scheme("https").queryParam("a", "1").build().toString()).isEqualTo("https://localhost:8443?a=1");
		
	}
	
	@Test
	public void testBuilderBehavior() {
		OkUriBuilder b = new OkUriBuilder().uri("https://www.google.com");
		OkUriBuilder clone = b.clone();
		OkUriBuilder b2 = b.queryParam("a", "1");
		
		Assertions.assertThat(b2.build().toString()).contains("https://www.google.com?a=1");
		Assertions.assertThat(b.build().toString()).contains("https://www.google.com?a=1");
		Assertions.assertThat(clone.build().toString()).isEqualTo("https://www.google.com");
	}
	
	@Test
	public void testPath() {
		Assertions.assertThat(new OkUriBuilder().path("/hello").build().toString()).isEqualTo("/hello");
	}
}
