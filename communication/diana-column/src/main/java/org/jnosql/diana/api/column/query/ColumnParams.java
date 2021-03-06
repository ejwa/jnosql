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

import org.jnosql.diana.api.Value;
import org.jnosql.query.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

final class ColumnParams implements Params {

    private final List<ParamValue> parameters = new ArrayList<>();

    boolean isNotEmpty() {
        return !parameters.isEmpty();
    }

    Value add(String param) {
        ParamValue value = new ParamValue(param);
        parameters.add(value);
        return value;
    }

    List<String> getParametersNames() {
        return parameters.stream().map(ParamValue::getName).collect(toList());
    }

    @Override
    public String toString() {
        return parameters.stream().map(ParamValue::getName).collect(joining(","));
    }

    @Override
    public boolean isEmpty() {
        return getNames().isEmpty();
    }

    @Override
    public List<String> getNames() {
        return parameters.stream()
                .filter(ParamValue::isEmpty)
                .map(ParamValue::getName)
                .collect(toList());
    }

    @Override
    public void bind(String name, Object value) {
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(value, "value is required");
        parameters.stream().filter(p -> p.getName().equals(name)).forEach(p -> p.setValue(value));
    }
}