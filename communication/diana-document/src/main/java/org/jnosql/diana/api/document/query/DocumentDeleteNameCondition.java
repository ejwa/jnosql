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
package org.jnosql.diana.api.document.query;

/**
 * The base to delete name condition
 */
public interface DocumentDeleteNameCondition {

    /**
     * Creates the equals condition {@link org.jnosql.diana.api.Condition#EQUALS}
     *
     * @param value the value to the condition
     * @param <T>   the type
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere eq(T value);

    /**
     * Creates the like condition {@link org.jnosql.diana.api.Condition#LIKE}
     *
     * @param value the value to the condition
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    DocumentDeleteWhere like(String value);

    /**
     * Creates the greater than condition {@link org.jnosql.diana.api.Condition#GREATER_THAN}
     *
     * @param <T>   the type
     * @param value the value to the condition
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere gt(T value);

    /**
     * Creates the greater equals than condition {@link org.jnosql.diana.api.Condition#GREATER_EQUALS_THAN}
     *
     * @param <T>   the type
     * @param value the value to the condition
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere gte(T value);

    /**
     * Creates the lesser than condition {@link org.jnosql.diana.api.Condition#LESSER_THAN}
     *
     * @param <T>   the type
     * @param value the value to the condition
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere lt(T value);

    /**
     * Creates the lesser equals than condition {@link org.jnosql.diana.api.Condition#LESSER_EQUALS_THAN}
     *
     * @param <T>   the type
     * @param value the value to the condition
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere lte(T value);

    /**
     * Creates the between condition {@link org.jnosql.diana.api.Condition#EQUALS}
     *
     * @param <T>    the type
     * @param valueA the values within a given range
     * @param valueB the values within a given range
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when either valueA or valueB are null
     */
    <T> DocumentDeleteWhere between(T valueA, T valueB);

    /**
     * Creates in condition {@link org.jnosql.diana.api.Condition#IN}
     *
     * @param values the values
     * @param <T>    the type
     * @return the {@link DocumentDeleteWhere}
     * @throws NullPointerException when value is null
     */
    <T> DocumentDeleteWhere in(Iterable<T> values);

    /**
     * Creates the equals condition {@link org.jnosql.diana.api.Condition#NOT}
     *
     * @return {@link DocumentDeleteNotCondition}
     */
    DocumentDeleteNotCondition not();

}
