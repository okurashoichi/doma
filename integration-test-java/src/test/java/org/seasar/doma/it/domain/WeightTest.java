/*
 * Copyright Doma Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.seasar.doma.it.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.seasar.doma.internal.jdbc.scalar.Scalar;
import org.seasar.doma.it.IntegrationTestEnvironment;
import org.seasar.doma.jdbc.ClassHelper;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.domain.DomainType;
import org.seasar.doma.jdbc.domain.DomainTypeFactory;

@ExtendWith(IntegrationTestEnvironment.class)
public class WeightTest {

  @Test
  public void testDefaultValue(Config config) throws Exception {
    DomainType<Integer, Weight> domainType =
        DomainTypeFactory.getDomainType(Weight.class, new ClassHelper() {});
    Scalar<Integer, Weight> scalar = domainType.createScalar();
    Weight domain = scalar.get();
    assertNotNull(domain);
    assertNull(domain.getValue());
  }

  @Test
  public void testDefaultValue_Optional(Config config) {
    DomainType<Integer, Weight> domainType = DomainTypeFactory.getDomainType(Weight.class);
    Scalar<Integer, Optional<Weight>> scalar = domainType.createOptionalScalar();
    Optional<Weight> optional = scalar.get();
    assertNotNull(optional);
    assertFalse(optional.isPresent());
  }
}
