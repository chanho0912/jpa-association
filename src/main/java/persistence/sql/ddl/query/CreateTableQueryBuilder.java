package persistence.sql.ddl.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.Dialect;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

import java.util.Arrays;
import java.util.List;

public class CreateTableQueryBuilder {
    private final StringBuilder query;
    private final Logger logger = LoggerFactory.getLogger(CreateTableQueryBuilder.class);

    public CreateTableQueryBuilder(
            Dialect dialect,
            Class<?> entityClass,
            List<Queryable> associatedJoinColumns
    ) {
        this.query = new StringBuilder();

        TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("CREATE TABLE ").append(tableDefinition.getTableName());
        query.append(" (");

        tableDefinition.withIdColumns().forEach(column -> column.applyToCreateTableQuery(query, dialect));
        associatedJoinColumns
                .forEach(joinColumn -> joinColumn.applyToCreateTableQuery(query, dialect));

        definePrimaryKey(tableDefinition.getTableId(), query);

        query.append(");");
    }

    public String build() {
        final String sql = query.toString();
        logger.info("Generated Create Table SQL: {}", sql);
        return sql;
    }

    private void definePrimaryKey(TableId pk, StringBuilder query) {
        query.append("PRIMARY KEY (").append(pk.getColumnName()).append(")");
    }
}
