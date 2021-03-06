/*
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
 */
package org.jnosql.artemis.document.query;


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.query.RepositoryType;
import org.jnosql.artemis.reflection.DynamicQueryMethodReturn;
import org.jnosql.artemis.reflection.DynamicReturn;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

/**
 * The template method to {@link org.jnosql.artemis.Repository} to Document
 *
 * @param <T> the class type
 */
public abstract class AbstractDocumentRepositoryProxy<T> extends BaseDocumentRepository implements InvocationHandler {


    protected abstract Repository getRepository();

    protected abstract DocumentTemplate getTemplate();

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        RepositoryType type = RepositoryType.of(method);
        Class<?> typeClass = getClassMapping().getClassInstance();

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                DocumentQuery query = getQuery(method, args);
                DynamicReturn<?> dynamicReturn = DynamicReturn.builder()
                        .withClassSource(typeClass)
                        .withMethodSource(method).withList(() -> getTemplate().select(query))
                        .withSingleResult(() -> getTemplate().singleResult(query)).build();

                return dynamicReturn.execute();
            case FIND_ALL:
                DocumentQuery queryFindAll = select().from(getClassMapping().getName()).build();
                DynamicReturn<?> dynamicReturnFindAll = DynamicReturn.builder()
                        .withClassSource(typeClass)
                        .withMethodSource(method).withList(() -> getTemplate().select(queryFindAll))
                        .withSingleResult(() -> getTemplate().singleResult(queryFindAll)).build();
                return dynamicReturnFindAll.execute();
            case DELETE_BY:
                DocumentDeleteQuery documentDeleteQuery = getDeleteQuery(method, args);
                getTemplate().delete(documentDeleteQuery);
                return null;
            case OBJECT_METHOD:
                return method.invoke(this, args);
            case JNOSQL_QUERY:
                DynamicQueryMethodReturn methodReturn = DynamicQueryMethodReturn.builder()
                        .withArgs(args)
                        .withMethod(method)
                        .withTypeClass(typeClass)
                        .withPrepareConverter(q -> getTemplate().prepare(q))
                        .withQueryConverter(q -> getTemplate().query(q)).build();
                return methodReturn.execute();
            default:
                return Void.class;
        }
    }

}
