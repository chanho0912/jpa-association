package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final DeleteByIdQueryBuilder deleteByIdQueryBuilder = new DeleteByIdQueryBuilder();

    private final Logger logger = LoggerFactory.getLogger(EntityPersister.class);
    private final Map<Class<?>, TableDefinition> tableDefinitions;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = new HashMap<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasId(Object entity) {
        final TableDefinition tableDefinition = getTableDefinition(entity);
        return tableDefinition.hasId(entity);
    }

    public Serializable getEntityId(Object entity) {
        final TableDefinition tableDefinition = getTableDefinition(entity);
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public Object insert(Object entity) {
        final String query = insertQueryBuilder.build(entity);
        logger.info("Generated Insert query: {}", query);
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);
        bindId(id, entity);
        return entity;
    }

    private void bindId(Serializable id, Object entity) {
        try {
            final Class<?> entityClass = entity.getClass();
            final TableDefinition tableDefinition = getTableDefinition(entity);
            final TableId tableId = tableDefinition.getTableId();

            final Field objectDeclaredField = entityClass.getDeclaredField(tableId.getDeclaredName());

            final boolean wasAccessible = objectDeclaredField.canAccess(entity);
            if (!wasAccessible) {
                objectDeclaredField.setAccessible(true);
            }

            objectDeclaredField.set(entity, id);

            if (!wasAccessible) {
                objectDeclaredField.setAccessible(false);
            }

        } catch (ReflectiveOperationException e) {
            logger.error("Failed to copy row to {}", entity.getClass().getName(), e);
        }
    }

    public void update(Object entity) {
        final String query = updateQueryBuilder.build(entity);
        logger.info("Generated Update query: {}", query);
        jdbcTemplate.execute(query);
    }

    private TableDefinition getTableDefinition(Object entity) {
        if (!tableDefinitions.containsKey(entity.getClass())) {
            tableDefinitions.put(entity.getClass(), new TableDefinition(entity.getClass()));
        }
        return tableDefinitions.get(entity.getClass());
    }

    public void delete(Object entity) {
        String query = deleteByIdQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }
}
