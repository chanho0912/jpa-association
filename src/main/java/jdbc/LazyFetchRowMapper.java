package jdbc;

import common.ReflectionFieldAccessUtils;
import persistence.entity.EntityLazyLoader;
import persistence.proxy.PersistentList;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectQueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LazyFetchRowMapper<T> extends AbstractRowMapper<T> {
    private final Class<T> clazz;
    private final TableDefinition tableDefinition;
    private final JdbcTemplate jdbcTemplate;

    public LazyFetchRowMapper(Class<T> clazz, JdbcTemplate jdbcTemplate) {
        super(clazz);
        this.clazz = clazz;
        this.tableDefinition = new TableDefinition(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        List<TableAssociationDefinition> associations = tableDefinition.getAssociations();
        for (TableAssociationDefinition association : associations) {
            if (association.isEager()) {
                continue;
            }

            final Field collectionField = clazz.getDeclaredField(association.getFieldName());
            List proxy = createProxy(instance, association.getEntityClass(), jdbcTemplate);
            ReflectionFieldAccessUtils.accessAndSet(instance, collectionField, proxy);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> createProxy(Object instance, Class<E> elementType, JdbcTemplate jdbcTemplate) {
        return (List<E>) Proxy.newProxyInstance(
                PersistentList.class.getClassLoader(),
                new Class[]{List.class},
                new PersistentList<>(instance, createLazyLoader(elementType))
        );
    }

    private EntityLazyLoader createLazyLoader(Class<?> targetClass) {
        return ownerTableMapper -> {
            final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(targetClass);
            final String joinColumnName = ownerTableMapper.getJoinColumnName(targetClass);
            final Object joinColumnValue = ownerTableMapper.getValue(joinColumnName);

            final String query = queryBuilder
                    .where(joinColumnName, joinColumnValue.toString())
                    .build();

            return jdbcTemplate.query(query,
                    RowMapperFactory.getInstance().createRowMapper(targetClass, jdbcTemplate)
            );
        };
    }
}
