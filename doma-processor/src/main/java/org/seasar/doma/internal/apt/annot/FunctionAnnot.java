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
package org.seasar.doma.internal.apt.annot;

import static org.seasar.doma.internal.util.AssertionUtil.assertNonNullValue;

import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import org.seasar.doma.MapKeyNamingType;
import org.seasar.doma.internal.apt.AptIllegalStateException;
import org.seasar.doma.internal.apt.util.AnnotationValueUtil;
import org.seasar.doma.jdbc.SqlLogType;

public class FunctionAnnot extends AbstractAnnot {

  private static final String CATALOG = "catalog";

  private static final String SCHEMA = "schema";

  private static final String NAME = "name";

  private static final String QUOTE = "quote";

  private static final String QUERY_TIMEOUT = "queryTimeout";

  private static final String MAP_KEY_NAMING = "mapKeyNaming";

  private static final String ENSURE_RESULT_MAPPING = "ensureResultMapping";

  private static final String SQL_LOG = "sqlLog";

  private final AnnotationValue catalog;

  private final AnnotationValue schema;

  private final AnnotationValue name;

  private final AnnotationValue quote;

  private final AnnotationValue queryTimeout;

  private final AnnotationValue mapKeyNaming;

  private final AnnotationValue ensureResultMapping;

  private final AnnotationValue sqlLog;

  private final String defaultName;

  FunctionAnnot(
      AnnotationMirror annotationMirror, Map<String, AnnotationValue> values, String defaultName) {
    super(annotationMirror);
    this.catalog = assertNonNullValue(values, CATALOG);
    this.schema = assertNonNullValue(values, SCHEMA);
    this.name = assertNonNullValue(values, NAME);
    this.quote = assertNonNullValue(values, QUOTE);
    this.queryTimeout = assertNonNullValue(values, QUERY_TIMEOUT);
    this.mapKeyNaming = assertNonNullValue(values, MAP_KEY_NAMING);
    this.ensureResultMapping = assertNonNullValue(values, ENSURE_RESULT_MAPPING);
    this.sqlLog = assertNonNullValue(values, SQL_LOG);
    this.defaultName = defaultName;
  }

  public AnnotationValue getQueryTimeout() {
    return queryTimeout;
  }

  public AnnotationValue getMapKeyNaming() {
    return mapKeyNaming;
  }

  public AnnotationValue getSqlLog() {
    return sqlLog;
  }

  public String getCatalogValue() {
    String value = AnnotationValueUtil.toString(catalog);
    if (value == null) {
      throw new AptIllegalStateException(CATALOG);
    }
    return value;
  }

  public String getSchemaValue() {
    String value = AnnotationValueUtil.toString(schema);
    if (value == null) {
      throw new AptIllegalStateException(SCHEMA);
    }
    return value;
  }

  public String getNameValue() {
    String value = AnnotationValueUtil.toString(name);
    if (value == null || value.isEmpty()) {
      return defaultName;
    }
    return value;
  }

  public boolean getQuoteValue() {
    Boolean value = AnnotationValueUtil.toBoolean(quote);
    if (value == null) {
      throw new AptIllegalStateException(QUOTE);
    }
    return value;
  }

  public int getQueryTimeoutValue() {
    Integer value = AnnotationValueUtil.toInteger(queryTimeout);
    if (value == null) {
      throw new AptIllegalStateException(QUERY_TIMEOUT);
    }
    return value;
  }

  public MapKeyNamingType getMapKeyNamingValue() {
    VariableElement enumConstant = AnnotationValueUtil.toEnumConstant(mapKeyNaming);
    if (enumConstant == null) {
      throw new AptIllegalStateException(MAP_KEY_NAMING);
    }
    return MapKeyNamingType.valueOf(enumConstant.getSimpleName().toString());
  }

  public boolean getEnsureResultMappingValue() {
    Boolean value = AnnotationValueUtil.toBoolean(ensureResultMapping);
    if (value == null) {
      throw new AptIllegalStateException(ENSURE_RESULT_MAPPING);
    }
    return value;
  }

  public SqlLogType getSqlLogValue() {
    VariableElement enumConstant = AnnotationValueUtil.toEnumConstant(sqlLog);
    if (enumConstant == null) {
      throw new AptIllegalStateException(SQL_LOG);
    }
    return SqlLogType.valueOf(enumConstant.getSimpleName().toString());
  }
}
