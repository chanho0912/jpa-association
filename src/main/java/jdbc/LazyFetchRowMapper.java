package jdbc;

import common.ReflectionFieldAccessUtils;
import persistence.proxy.ProxyFactory;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.lang.reflect.Field;
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
            List proxy = ProxyFactory.getProxy(instance, association.getEntityClass(), jdbcTemplate);
            ReflectionFieldAccessUtils.accessAndSet(instance, collectionField, proxy);
        }
    }
}
