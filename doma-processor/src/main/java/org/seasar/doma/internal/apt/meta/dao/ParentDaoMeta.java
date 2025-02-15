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
package org.seasar.doma.internal.apt.meta.dao;

import static org.seasar.doma.internal.util.AssertionUtil.assertNotNull;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.seasar.doma.internal.apt.annot.DaoAnnot;

public class ParentDaoMeta {

  private final DaoAnnot daoAnnot;

  private final TypeElement typeElement;

  private final List<ExecutableElement> methods;

  ParentDaoMeta(DaoAnnot daoAnnot, TypeElement typeElement, List<ExecutableElement> methods) {
    assertNotNull(daoAnnot, typeElement);
    this.daoAnnot = daoAnnot;
    this.typeElement = typeElement;
    this.methods = methods;
  }

  public TypeElement getTypeElement() {
    return typeElement;
  }

  public List<ExecutableElement> getMethods() {
    return methods;
  }
}
