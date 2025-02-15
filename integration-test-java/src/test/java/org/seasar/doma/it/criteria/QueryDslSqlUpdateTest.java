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
package org.seasar.doma.it.criteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.seasar.doma.it.criteria.CustomExpressions.addSalaryUserDefined;
import static org.seasar.doma.it.criteria.CustomExpressions.concatWithUserDefined;
import static org.seasar.doma.jdbc.criteria.expression.Expressions.add;
import static org.seasar.doma.jdbc.criteria.expression.Expressions.concat;
import static org.seasar.doma.jdbc.criteria.expression.Expressions.literal;
import static org.seasar.doma.jdbc.criteria.expression.Expressions.max;
import static org.seasar.doma.jdbc.criteria.expression.Expressions.select;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.seasar.doma.it.Dbms;
import org.seasar.doma.it.IntegrationTestEnvironment;
import org.seasar.doma.it.Run;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.criteria.QueryDsl;
import org.seasar.doma.jdbc.criteria.expression.SelectExpression;
import org.seasar.doma.jdbc.criteria.statement.EmptyWhereClauseException;

@ExtendWith(IntegrationTestEnvironment.class)
public class QueryDslSqlUpdateTest {

  private final QueryDsl dsl;

  public QueryDslSqlUpdateTest(Config config) {
    this.dsl = new QueryDsl(config);
  }

  @Test
  void settings() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(
                e,
                settings -> {
                  settings.setComment("update all");
                  settings.setQueryTimeout(1000);
                  settings.setSqlLogType(SqlLogType.RAW);
                  settings.setAllowEmptyWhere(true);
                  settings.setBatchSize(20);
                })
            .set(c -> c.value(e.employeeName, "aaa"))
            .execute();

    assertEquals(14, count);
  }

  @Test
  void where() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e)
            .set(c -> c.value(e.departmentId, 3))
            .where(
                c -> {
                  c.isNotNull(e.managerId);
                  c.ge(e.salary, new Salary("2000"));
                })
            .execute();

    assertEquals(5, count);
  }

  @Test
  void where_empty() {
    Employee_ e = new Employee_();

    EmptyWhereClauseException ex =
        assertThrows(
            EmptyWhereClauseException.class,
            () -> dsl.update(e).set(c -> c.value(e.departmentId, 3)).execute());
    System.out.println(ex.getMessage());
  }

  @Test
  void where_empty_allowEmptyWhere_enabled() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e, settings -> settings.setAllowEmptyWhere(true))
            .set(c -> c.value(e.departmentId, 3))
            .execute();

    assertEquals(14, count);
  }

  @Test
  void expression_add() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e)
            .set(c -> c.value(e.version, add(e.version, 10)))
            .where(c -> c.eq(e.employeeId, 1))
            .execute();

    assertEquals(1, count);

    Employee employee = dsl.from(e).where(c -> c.eq(e.employeeId, 1)).fetchOne();
    assertNotNull(employee);
    assertEquals(11, employee.getVersion());
  }

  @Test
  void expression_concat() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e)
            .set(c -> c.value(e.employeeName, concat("[", concat(e.employeeName, "]"))))
            .where(c -> c.eq(e.employeeId, 1))
            .execute();

    assertEquals(1, count);
  }

  @Test
  void expressions() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e)
            .set(
                c -> {
                  c.value(e.employeeName, concat("[", concat(e.employeeName, "]")));
                  c.value(e.version, add(e.version, literal(1)));
                })
            .where(c -> c.eq(e.employeeId, 1))
            .execute();

    assertEquals(1, count);
  }

  @Test
  @Run(unless = {Dbms.MYSQL, Dbms.MYSQL8})
  void expression_userDefined() {
    Employee_ e = new Employee_();

    int count =
        dsl.update(e)
            .set(
                c -> {
                  c.value(
                      e.employeeName,
                      concatWithUserDefined(literal("["), e.employeeName, literal("]")));
                  c.value(e.salary, addSalaryUserDefined(e));
                })
            .where(c -> c.eq(e.employeeId, 1))
            .execute();

    assertEquals(1, count);

    Employee result = dsl.from(e).where(c -> c.eq(e.employeeId, 1)).fetchOne();
    assertEquals("[-SMITH-]", result.getEmployeeName());
    assertEquals(0, result.getSalary().getValue().compareTo(new BigDecimal(900)));
  }

  @Test
  @Run(unless = {Dbms.MYSQL, Dbms.MYSQL8})
  void expression_select() {
    Employee_ e = new Employee_();
    Employee_ e2 = new Employee_();
    Department_ d = new Department_();

    SelectExpression<Salary> subSelect =
        select(
            c ->
                c.from(e2)
                    .innerJoin(d, on -> on.eq(e2.departmentId, d.departmentId))
                    .where(cc -> cc.eq(e.departmentId, d.departmentId))
                    .groupBy(d.departmentId)
                    .select(max(e2.salary)));

    int count =
        dsl.update(e)
            .set(c -> c.value(e.salary, subSelect))
            .where(c -> c.eq(e.employeeId, 1))
            .peek(System.out::println)
            .execute();

    assertEquals(1, count);

    Salary salary = dsl.from(e).where(c -> c.eq(e.employeeId, 1)).select(e.salary).fetchOne();
    assertEquals(0, new BigDecimal("3000").compareTo(salary.getValue()));
  }
}
