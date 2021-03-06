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
package org.jnosql.artemis.key;

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassMapping;
import org.jnosql.artemis.reflection.ClassMappings;
import org.jnosql.artemis.reflection.FieldMapping;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.KeyValueEntity;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Template method to {@link KeyValueEntityConverter}
 */
public abstract class AbstractKeyValueEntityConverter implements KeyValueEntityConverter {

    protected abstract ClassMappings getClassMappings();

    protected abstract Converters getConverters();

    @Override
    public KeyValueEntity<?> toKeyValue(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        Class<?> clazz = entityInstance.getClass();

        FieldMapping key = getId(clazz);
        Object value = key.read(entityInstance);

        requireNonNull(value, String.format("The key field %s is required", key.getName()));
        return KeyValueEntity.of(getKey(value, clazz, false), entityInstance);
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, KeyValueEntity<?> entity) {

        Value value = entity.getValue();
        T bean = value.get(entityClass);
        if (Objects.isNull(bean)) {
            return null;
        }

        Object key = getKey(entity.getKey(), entityClass, true);
        FieldMapping id = getId(entityClass);
        id.write(bean, key);
        return bean;
    }

    private <T> Object getKey(Object key, Class<T> entityClass, boolean toEntity) {
        FieldMapping id = getId(entityClass);
        if (id.getConverter().isPresent()) {
            AttributeConverter attributeConverter = getConverters().get(id.getConverter().get());
            if(toEntity) {
                return attributeConverter.convertToEntityAttribute(key);
            } else {
                return attributeConverter.convertToDatabaseColumn(key);
            }
        } else {
            return Value.of(key).get(id.getNativeField().getType());
        }
    }

    private FieldMapping getId(Class<?> clazz) {
        ClassMapping mapping = getClassMappings().get(clazz);
        return mapping.getId().orElseThrow(() -> IdNotFoundException.newInstance(clazz));
    }
}
