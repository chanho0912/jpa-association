package persistence.entity;

import jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
import persistence.sql.AliasUtils;
import persistence.sql.Queryable;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class EntityRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final TableDefinition tableDefinition;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.tableDefinition = new TableDefinition(clazz);
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            final T instance = newInstance(clazz);

            for (Queryable field : tableDefinition.withIdColumns()) {
                setField(resultSet, clazz, field, instance);
            }

            do {
                List<TableAssociationDefinition> associations = tableDefinition.getAssociations();
                if (associations.isEmpty()) {
                    return instance;
                }

                for (TableAssociationDefinition association : associations) {
                    if (!association.isFetchEager()) {
                        continue;
                    }
                    final Object associatedInstance = association.getAssociatedEntityClass().getDeclaredConstructor().newInstance();
                    for (Queryable field : association.getAssociatedTableDefinition().withIdColumns()) {
                        setField(resultSet, association.getAssociatedEntityClass(), field, associatedInstance);
                    }

                    final Collection<Object> entityCollection = getCollectionField(instance, association);
                    entityCollection.add(associatedInstance);
                }
            } while (resultSet.next());
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to map row to " + clazz.getName(), e);
        }
    }

    @NotNull
    private T newInstance(
            Class<T> clazz
    ) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    private Collection<Object> getCollectionField(
            T instance, TableAssociationDefinition collection
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
                          Queryable field, Object instance) throws NoSuchFieldException, SQLException, IllegalAccessException {
        final String databaseColumnName = field.getColumnName();
        final Field objectDeclaredField = entityClass.getDeclaredField(field.getDeclaredName());
        final String tableName = new TableDefinition(entityClass).getTableName();

        final boolean wasAccessible = objectDeclaredField.canAccess(instance);
        if (!wasAccessible) {
            objectDeclaredField.setAccessible(true);
        }

        objectDeclaredField.set(instance, resultSet.getObject(AliasUtils.alias(tableName, databaseColumnName)));

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
