package jdbc;

import persistence.sql.definition.TableDefinition;

public class RowMapperFactory {
    private RowMapperFactory() {
    }

    public static <T> RowMapper<T> createRowMapper(Class<T> targetClass, JdbcTemplate jdbcTemplate) {
        final TableDefinition tableDefinition = new TableDefinition(targetClass);
        for (var association : tableDefinition.getAssociations()) {
            if (association.isEager()) {
                return new EagerFetchRowMapper<>(targetClass);
            }
        }

        return new LazyFetchRowMapper<>(targetClass, jdbcTemplate);
    }

}
