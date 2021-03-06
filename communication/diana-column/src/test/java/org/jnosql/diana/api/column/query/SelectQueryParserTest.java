/*
 *
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */
package org.jnosql.diana.api.column.query;

import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnObserverParser;
import org.jnosql.diana.api.column.ColumnPreparedStatement;
import org.jnosql.diana.api.column.ColumnPreparedStatementAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.query.QueryException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.diana.api.Condition.AND;
import static org.jnosql.diana.api.Condition.BETWEEN;
import static org.jnosql.diana.api.Condition.EQUALS;
import static org.jnosql.diana.api.Condition.GREATER_EQUALS_THAN;
import static org.jnosql.diana.api.Condition.GREATER_THAN;
import static org.jnosql.diana.api.Condition.IN;
import static org.jnosql.diana.api.Condition.LESSER_EQUALS_THAN;
import static org.jnosql.diana.api.Condition.LESSER_THAN;
import static org.jnosql.diana.api.Condition.LIKE;
import static org.jnosql.diana.api.Condition.NOT;
import static org.jnosql.diana.api.Condition.OR;
import static org.jnosql.diana.api.Sort.SortType.ASC;
import static org.jnosql.diana.api.Sort.SortType.DESC;
import static org.jnosql.diana.api.column.ColumnCondition.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectQueryParserTest {

    private SelectQueryParser parser = new SelectQueryParser();

    private ColumnFamilyManager manager = Mockito.mock(ColumnFamilyManager.class);
    private ColumnFamilyManagerAsync managerAsync = Mockito.mock(ColumnFamilyManagerAsync.class);
    private final ColumnObserverParser observer = new ColumnObserverParser() {
    };


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select name, address from God"})
    public void shouldReturnParserQuery1(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertThat(columnQuery.getColumns(), contains("name", "address"));
        assertTrue(columnQuery.getSorts().isEmpty());
        assertEquals(0L, columnQuery.getLimit());
        assertEquals(0L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God order by name"})
    public void shouldReturnParserQuery3(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertTrue(columnQuery.getColumns().isEmpty());
        assertThat(columnQuery.getSorts(), contains(Sort.of("name", ASC)));
        assertEquals(0L, columnQuery.getLimit());
        assertEquals(0L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God order by name asc"})
    public void shouldReturnParserQuery4(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertTrue(columnQuery.getColumns().isEmpty());
        assertThat(columnQuery.getSorts(), contains(Sort.of("name", ASC)));
        assertEquals(0L, columnQuery.getLimit());
        assertEquals(0L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God order by name desc"})
    public void shouldReturnParserQuery5(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertTrue(columnQuery.getColumns().isEmpty());
        assertThat(columnQuery.getSorts(), contains(Sort.of("name", DESC)));
        assertEquals(0L, columnQuery.getLimit());
        assertEquals(0L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God order by name desc age asc"})
    public void shouldReturnParserQuery6(String query) {

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertTrue(columnQuery.getColumns().isEmpty());
        assertThat(columnQuery.getSorts(), contains(Sort.of("name", DESC), Sort.of("age", ASC)));
        assertEquals(0L, columnQuery.getLimit());
        assertEquals(0L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God skip 12"})
    public void shouldReturnParserQuery7(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 12L);
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God limit 12"})
    public void shouldReturnParserQuery8(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 12L, 0L);
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God skip 10 limit 12"})
    public void shouldReturnParserQuery9(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        assertTrue(columnQuery.getColumns().isEmpty());
        assertTrue(columnQuery.getSorts().isEmpty());
        assertEquals(12L, columnQuery.getLimit());
        assertEquals(10L, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
        assertFalse(columnQuery.getCondition().isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = 10"})
    public void shouldReturnParserQuery10(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(EQUALS, condition.getCondition());
        assertEquals(Column.of("age", 10L), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where stamina > 10.23"})
    public void shouldReturnParserQuery11(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(GREATER_THAN, condition.getCondition());
        assertEquals(Column.of("stamina", 10.23), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where stamina >= -10.23"})
    public void shouldReturnParserQuery12(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(GREATER_EQUALS_THAN, condition.getCondition());
        assertEquals(Column.of("stamina", -10.23), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where stamina <= -10.23"})
    public void shouldReturnParserQuery13(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(LESSER_EQUALS_THAN, condition.getCondition());
        assertEquals(Column.of("stamina", -10.23), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where stamina < -10.23"})
    public void shouldReturnParserQuery14(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(LESSER_THAN, condition.getCondition());
        assertEquals(Column.of("stamina", -10.23), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age between 10 and 30"})
    public void shouldReturnParserQuery15(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(BETWEEN, condition.getCondition());
        assertEquals(Column.of("age", Arrays.asList(10L, 30L)), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"diana\""})
    public void shouldReturnParserQuery16(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "diana"), condition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where siblings = {\"apollo\": \"Brother\", \"Zeus\": \"Father\"}"})
    public void shouldReturnParserQuery18(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();

        assertEquals(EQUALS, condition.getCondition());
        Column column = condition.getColumn();
        List<Column> columns = column.get(new TypeReference<List<Column>>() {
        });
        assertThat(columns, containsInAnyOrder(Column.of("apollo", "Brother"),
                Column.of("Zeus", "Father")));
        assertEquals("siblings", column.getName());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = convert(12, java.lang.Integer)"})
    public void shouldReturnParserQuery19(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(EQUALS, condition.getCondition());
        assertEquals("age", column.getName());
        assertEquals(Value.of(12), column.getValue());


    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name in (\"Ada\", \"Apollo\")"})
    public void shouldReturnParserQuery20(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(IN, condition.getCondition());
        assertEquals("name", column.getName());
        List<String> values = column.get(new TypeReference<List<String>>() {
        });
        assertThat(values, containsInAnyOrder("Ada", "Apollo"));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God where name like \"Ada\""})
    public void shouldReturnParserQuery21(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(LIKE, condition.getCondition());
        assertEquals("name", column.getName());
        assertEquals("Ada", column.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select * from God where name not like \"Ada\""})
    public void shouldReturnParserQuery22(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(NOT, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        ColumnCondition columnCondition = conditions.get(0);
        assertEquals(LIKE, columnCondition.getCondition());
        assertEquals(Column.of("name", "Ada"), columnCondition.getColumn());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"Ada\" and age = 20"})
    public void shouldReturnParserQuery23(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(AND, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertThat(conditions, contains(eq(Column.of("name", "Ada")),
                eq(Column.of("age", 20L))));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"Ada\" or age = 20"})
    public void shouldReturnParserQuery24(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(OR, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertThat(conditions, contains(eq(Column.of("name", "Ada")),
                eq(Column.of("age", 20L))));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"Ada\" and age = 20 or" +
            " siblings = {\"apollo\": \"Brother\", \"Zeus\": \"Father\"}"})
    public void shouldReturnParserQuery25(String query) {

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(AND, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertEquals(EQUALS, conditions.get(0).getCondition());
        assertEquals(EQUALS, conditions.get(1).getCondition());
        assertEquals(OR, conditions.get(2).getCondition());

    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"Ada\" and age = 20 or" +
            " siblings = {\"apollo\": \"Brother\", \"Zeus\": \"Father\"} and birthday =" +
            " convert(\"2007-12-03\", java.time.LocalDate)"})
    public void shouldReturnParserQuery26(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        parser.query(query, manager, observer);
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(AND, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertEquals(EQUALS, conditions.get(0).getCondition());
        assertEquals(EQUALS, conditions.get(1).getCondition());
        assertEquals(OR, conditions.get(2).getCondition());
        assertEquals(EQUALS, conditions.get(3).getCondition());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldReturnErrorWhenIsQueryWithParam(String query) {

        assertThrows(QueryException.class, () -> parser.query(query, manager, observer));


    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldReturnErrorWhenDontBindParameters(String query) {

        ColumnPreparedStatement prepare = parser.prepare(query, manager, observer);
        assertThrows(QueryException.class, prepare::getResultList);
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldExecutePrepareStatment(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);

        ColumnPreparedStatement prepare = parser.prepare(query, manager, observer);
        prepare.bind("age", 12);
        prepare.getResultList();
        Mockito.verify(manager).select(captor.capture());
        ColumnQuery columnQuery = captor.getValue();
        ColumnCondition columnCondition = columnQuery.getCondition().get();
        Column column = columnCondition.getColumn();
        assertEquals(EQUALS, columnCondition.getCondition());
        assertEquals("age", column.getName());
        assertEquals(12, column.get());
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldReturnErrorWhenIsQueryWithParamAsync(String query) {

        assertThrows(QueryException.class, () -> parser.queryAsync(query, managerAsync, s -> {
        }, observer));


    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldReturnErrorWhenDontBindParametersAsync(String query) {

        ColumnPreparedStatementAsync prepare = parser.prepareAsync(query, managerAsync, observer);
        assertThrows(QueryException.class, () -> prepare.getResultList(s -> {
        }));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where age = @age"})
    public void shouldExecutePrepareStatmentAsync(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);

        ColumnPreparedStatementAsync prepare = parser.prepareAsync(query, managerAsync, observer);
        prepare.bind("age", 12);
        Consumer<List<ColumnEntity>> callBack = s -> {
        };
        prepare.getResultList(callBack);
        Mockito.verify(managerAsync).select(captor.capture(), Mockito.eq(callBack));
        ColumnQuery columnQuery = captor.getValue();
        ColumnCondition columnCondition = columnQuery.getCondition().get();
        Column column = columnCondition.getColumn();
        assertEquals(EQUALS, columnCondition.getCondition());
        assertEquals("age", column.getName());
        assertEquals(12, column.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"select  * from God where name = \"Ada\" and age = 20"})
    public void shouldReturnParserQueryAsync(String query) {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        Consumer<List<ColumnEntity>> callBack = s -> {
        };
        parser.queryAsync(query, managerAsync, callBack, observer);
        Mockito.verify(managerAsync).select(captor.capture(), Mockito.eq(callBack));
        ColumnQuery columnQuery = captor.getValue();

        checkBaseQuery(columnQuery, 0L, 0L);
        assertTrue(columnQuery.getCondition().isPresent());
        ColumnCondition condition = columnQuery.getCondition().get();
        Column column = condition.getColumn();
        assertEquals(AND, condition.getCondition());
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertThat(conditions, contains(eq(Column.of("name", "Ada")),
                eq(Column.of("age", 20L))));
    }


    private void checkBaseQuery(ColumnQuery columnQuery, long limit, long skip) {
        assertTrue(columnQuery.getColumns().isEmpty());
        assertTrue(columnQuery.getSorts().isEmpty());
        assertEquals(limit, columnQuery.getLimit());
        assertEquals(skip, columnQuery.getSkip());
        assertEquals("God", columnQuery.getColumnFamily());
    }
}
