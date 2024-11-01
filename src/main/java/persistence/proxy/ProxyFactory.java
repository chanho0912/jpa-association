package persistence.proxy;

import jdbc.JdbcTemplate;
import jdbc.RowMapperFactory;
import persistence.entity.EntityLazyLoader;
import persistence.sql.definition.EntityTableMapper;
import persistence.sql.dml.query.SelectQueryBuilder;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

public class ProxyFactory {
    private ProxyFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getProxy(Object instance, Class<T> elementType, JdbcTemplate jdbcTemplate) {
        return (List<T>) Proxy.newProxyInstance(
                PersistentList.class.getClassLoader(),
                new Class[]{List.class},
                new PersistentList<>(instance, elementType, defaultLazyLoader(jdbcTemplate))
        );
    }

    private static EntityLazyLoader defaultLazyLoader(JdbcTemplate jdbcTemplate) {
        return new EntityLazyLoader() {
            @Override
            public <T> Collection<T> loadLazyCollection(Class<T> targetClass, EntityTableMapper ownerTableMapper) {
                final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(targetClass);
                final String joinColumnName = ownerTableMapper.getJoinColumnName(targetClass);
                final Object joinColumnValue = ownerTableMapper.getValue(joinColumnName);

                final String query = queryBuilder
                        .where(joinColumnName, joinColumnValue.toString())
                        .build();

                return jdbcTemplate.query(query,
                        RowMapperFactory.createRowMapper(targetClass, jdbcTemplate)
                );
            }
        };
    }
}
