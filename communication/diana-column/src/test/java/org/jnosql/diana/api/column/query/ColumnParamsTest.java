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

import org.hamcrest.MatcherAssert;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.query.QueryException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColumnParamsTest {



    @Test
    public void shouldAddParamter() {
        ColumnParams params = new ColumnParams();
        Value name = params.add("name");
        assertNotNull(name);
        MatcherAssert.<List<String>>assertThat(params.getParametersNames(), containsInAnyOrder("name"));
    }

    @Test
    public void shouldNotUseValueWhenIsInvalid() {
        ColumnParams params = new ColumnParams();
        Value name = params.add("name");
        assertThrows(QueryException.class, name::get);

        assertThrows(QueryException.class, () -> name.get(String.class));
    }

    @Test
    public void shouldSetParameter() {
        ColumnParams params = new ColumnParams();
        Value name = params.add("name");
        Column column = Column.of("name", name);
        params.bind("name", "Ada Lovelace");

        assertEquals("Ada Lovelace", column.get());

        params.bind("name", "Diana");
        assertEquals("Diana", column.get());
    }

}