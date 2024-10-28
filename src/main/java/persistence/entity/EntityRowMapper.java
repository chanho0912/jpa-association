package persistence.entity;

import common.AliasRule;
import common.ReflectionFieldAccessUtils;
import jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
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
            final T instance = clazz.getDeclaredConstructor().newInstance();

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

    private Collection<Object> getCollectionField(
            T instance, TableAssociationDefinition collection
    ) throws NoSuchFieldException, IllegalAccessException {
        final Field collectionField = clazz.getDeclaredField(collection.getFieldName());

        Collection<Object> entityCollection = (Collection<Object>) ReflectionFieldAccessUtils.accessAndGet(instance, collectionField);
        if (entityCollection == null) {
            entityCollection = new ArrayList<>();
            collectionField.set(instance, entityCollection);
        }

        return entityCollection;
    }

    private void setField(ResultSet resultSet, Class<?> entityClass,
                          Queryable field, Object instance) throws NoSuchFieldException, SQLException, IllegalAccessException {
        final String databaseColumnName = field.getColumnName();
        final Field objectDeclaredField = entityClass.getDeclaredField(field.getDeclaredName());
        final String tableName = new TableDefinition(entityClass).getTableName();
        final Object bindValue = resultSet.getObject(AliasRule.buildWith(tableName, databaseColumnName));

        ReflectionFieldAccessUtils.accessAndSet(instance, objectDeclaredField, bindValue);
    }

}
