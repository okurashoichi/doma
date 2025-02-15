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
package org.seasar.doma.jdbc.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import example.entity.Emp;
import example.entity._Emp;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.jdbc.InParameter;
import org.seasar.doma.jdbc.PreparedSql;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.dialect.PostgresDialect;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AutoBatchInsertQueryTest {

  private final MockConfig runtimeConfig = new MockConfig();

  private Method method;

  @BeforeEach
  protected void setUp(TestInfo testInfo) {
    method = testInfo.getTestMethod().get();
  }

  @Test
  public void testPrepare() {
    Emp emp1 = new Emp();
    emp1.setId(10);
    emp1.setName("aaa");

    Emp emp2 = new Emp();
    emp2.setId(20);
    emp2.setName("bbb");

    AutoBatchInsertQuery<Emp> query = new AutoBatchInsertQuery<>(_Emp.getSingletonInternal());
    query.setMethod(method);
    query.setConfig(runtimeConfig);
    query.setCallerClassName("aaa");
    query.setCallerMethodName("bbb");
    query.setEntities(Arrays.asList(emp1, emp2));
    query.setSqlLogType(SqlLogType.FORMATTED);
    query.prepare();

    assertTrue(query.isBatchSupported());
    assertEquals(2, query.getSqls().size());
  }

  @Test
  public void testOption_default() {
    Emp emp1 = new Emp();
    emp1.setId(10);
    emp1.setName("aaa");

    Emp emp2 = new Emp();
    emp2.setId(20);
    emp2.setSalary(new BigDecimal(2000));
    emp2.setVersion(10);

    AutoBatchInsertQuery<Emp> query = new AutoBatchInsertQuery<>(_Emp.getSingletonInternal());
    query.setMethod(method);
    query.setConfig(runtimeConfig);
    query.setCallerClassName("aaa");
    query.setCallerMethodName("bbb");
    query.setEntities(Arrays.asList(emp1, emp2));
    query.setSqlLogType(SqlLogType.FORMATTED);
    query.prepare();

    PreparedSql sql = query.getSqls().get(0);
    assertEquals(
        "insert into EMP (ID, NAME, SALARY, VERSION) values (?, ?, ?, ?)", sql.getRawSql());
    List<InParameter<?>> parameters = sql.getParameters();
    assertEquals(4, parameters.size());
    assertEquals(10, parameters.get(0).getWrapper().get());
    assertEquals("aaa", parameters.get(1).getWrapper().get());
    assertNull(parameters.get(2).getWrapper().get());
    assertEquals(1, parameters.get(3).getWrapper().get());

    sql = query.getSqls().get(1);
    assertEquals(
        "insert into EMP (ID, NAME, SALARY, VERSION) values (?, ?, ?, ?)", sql.getRawSql());
    parameters = sql.getParameters();
    assertEquals(4, parameters.size());
    assertEquals(20, parameters.get(0).getWrapper().get());
    assertNull(parameters.get(1).getWrapper().get());
    assertEquals(new BigDecimal(2000), parameters.get(2).getWrapper().get());
    assertEquals(10, parameters.get(3).getWrapper().get());
  }

  @Test
  public void testOption_duplicateKeys() {
    runtimeConfig.dialect = new PostgresDialect();

    Emp emp1 = new Emp();
    emp1.setId(10);
    emp1.setName("aaa");

    Emp emp2 = new Emp();
    emp2.setId(20);
    emp2.setSalary(new BigDecimal(2000));
    emp2.setVersion(10);

    AutoBatchInsertQuery<Emp> query = new AutoBatchInsertQuery<>(_Emp.getSingletonInternal());
    query.setMethod(method);
    query.setConfig(runtimeConfig);
    query.setDuplicateKeyType(DuplicateKeyType.UPDATE);
    query.setDuplicateKeyNames("name");
    query.setCallerClassName("aaa");
    query.setCallerMethodName("bbb");
    query.setEntities(Arrays.asList(emp1, emp2));
    query.setSqlLogType(SqlLogType.FORMATTED);
    query.prepare();

    PreparedSql sql = query.getSqls().get(0);
    assertEquals(
        "insert into EMP as target (ID, NAME, SALARY, VERSION) values (?, ?, ?, ?) on conflict (NAME) do update set SALARY = excluded.SALARY, VERSION = excluded.VERSION",
        sql.getRawSql());
    List<InParameter<?>> parameters = sql.getParameters();
    assertEquals(4, parameters.size());
    assertEquals(10, parameters.get(0).getWrapper().get());
    assertEquals("aaa", parameters.get(1).getWrapper().get());
    assertNull(parameters.get(2).getWrapper().get());
    assertEquals(1, parameters.get(3).getWrapper().get());

    sql = query.getSqls().get(1);
    assertEquals(
        "insert into EMP as target (ID, NAME, SALARY, VERSION) values (?, ?, ?, ?) on conflict (NAME) do update set SALARY = excluded.SALARY, VERSION = excluded.VERSION",
        sql.getRawSql());
    parameters = sql.getParameters();
    assertEquals(4, parameters.size());
    assertEquals(20, parameters.get(0).getWrapper().get());
    assertNull(parameters.get(1).getWrapper().get());
    assertEquals(new BigDecimal(2000), parameters.get(2).getWrapper().get());
    assertEquals(10, parameters.get(3).getWrapper().get());
  }
}
