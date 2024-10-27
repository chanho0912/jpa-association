package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.CustomSelectQueryBuilder;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(EntityLoader.class);

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final CustomSelectQueryBuilder queryBuilder = new CustomSelectQueryBuilder(entityKey.getEntityClass());
        final TableDefinition tableDefinition = new TableDefinition(entityKey.getEntityClass());
        tableDefinition.getAssociations().forEach(association -> {
            if (association.isFetchEager()) {
                queryBuilder.join(association);
            }
        });

        final String query = queryBuilder.build();
        logger.info("Executing custom select query: {}", query);

        final Object queried = jdbcTemplate.queryForObject(query,
                new EntityRowMapper<>(entityKey.getEntityClass())
        );

        return entityClass.cast(queried);
    }
}
