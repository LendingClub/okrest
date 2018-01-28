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

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.okrest3.OkRestException;
import io.macgyver.okrest3.OkRestWrapperException;

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
