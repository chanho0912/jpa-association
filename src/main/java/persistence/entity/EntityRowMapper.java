package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.Queryable;
import persistence.sql.definition.TableCollectionDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.AliasRule;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class EntityRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            final TableDefinition tableDefinition = new TableDefinition(clazz);
            final T instance = clazz.getDeclaredConstructor().newInstance();

            for (Queryable field : tableDefinition.withIdColumns()) {
                setField(resultSet, clazz, field, instance,
                        (columnName) -> AliasRule.getColumnAlias(tableDefinition, columnName));
            }

            for (TableCollectionDefinition collection : tableDefinition.getCollectionColumns()) {
                final Object collectionInstance = collection.getEntityClass().getDeclaredConstructor().newInstance();
                for (Queryable field : collection.getAssociatedTableDefinition().withIdColumns()) {
                    setField(resultSet, collection.getEntityClass(), field, collectionInstance,
                            (columnName) -> AliasRule.getJoinColumnAlias(collection.getAssociatedTableDefinition(), columnName));
                }

                final Collection<Object> entityCollection = getCollectionField(instance, collection);

                if (isRowEmpty(resultSet,
                        collection.getAssociatedTableDefinition().withIdColumns()
                                .stream()
                                .map(queryable -> AliasRule.getJoinColumnAlias(collection.getAssociatedTableDefinition(),
                                        queryable.getColumnName()))
                                .toList())
                ) {
                    continue;
                }
                entityCollection.add(collectionInstance);
            }

            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to map row to " + clazz.getName(), e);
        }
    }

    private Collection<Object> getCollectionField(
            T instance, TableCollectionDefinition collection
    ) throws NoSuchFieldException, IllegalAccessException {
        final Field collectionField = clazz.getDeclaredField(collection.getFieldName());
        final boolean wasAccessible = collectionField.canAccess(instance);
        if (!wasAccessible) {
            collectionField.setAccessible(true);
        }

        Collection<Object> entityCollection = (Collection<Object>) collectionField.get(instance);
        if (entityCollection == null) {
            entityCollection = new ArrayList<>();
            collectionField.set(instance, entityCollection);
        }

        if (!wasAccessible) {
            collectionField.setAccessible(false);
        }

        return entityCollection;
    }

    private void setField(ResultSet resultSet, Class<?> entityClass,
                          Queryable field, Object instance,
                          Function<String, String> aliasTransfer) throws NoSuchFieldException, SQLException, IllegalAccessException {
        final String databaseColumnName = field.getColumnName();
        final Field objectDeclaredField = entityClass.getDeclaredField(field.getDeclaredName());

        final boolean wasAccessible = objectDeclaredField.canAccess(instance);
        if (!wasAccessible) {
            objectDeclaredField.setAccessible(true);
        }

        objectDeclaredField.set(instance, resultSet.getObject(aliasTransfer.apply(databaseColumnName)));

        if (!wasAccessible) {
            objectDeclaredField.setAccessible(false);
        }
    }

    private boolean isRowEmpty(ResultSet resultSet, Collection<String> columns) throws SQLException {
        for (String column : columns) {
            if (resultSet.getObject(column) != null) {
                return false;
            }
        }
        return true;
    }
}
