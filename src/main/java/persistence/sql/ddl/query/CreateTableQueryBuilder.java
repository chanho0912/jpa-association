package persistence.sql.ddl.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.Dialect;
import persistence.sql.Queryable;
import persistence.sql.definition.ColumnDefinition;
import persistence.sql.definition.JoinColumnDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

import java.util.List;
import java.util.Objects;

public class CreateTableQueryBuilder {
    private final StringBuilder query;
    private final Logger logger = LoggerFactory.getLogger(CreateTableQueryBuilder.class);

    public CreateTableQueryBuilder(
            Dialect dialect,
            Class<?> entityClass,
            Class<?> associatedClass
    ) {
        this.query = new StringBuilder();

        TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("CREATE TABLE ").append(tableDefinition.getTableName());
        query.append(" (");

        tableDefinition.withIdColumns().forEach(column -> column.applyToCreateTableQuery(query, dialect));
        if (associatedClass != null) {
            TableDefinition associatedTableDefinition = new TableDefinition(associatedClass);
            List<JoinColumnDefinition> associatedJoinColumns = associatedTableDefinition.getJoinColumns();
            if (!associatedJoinColumns.isEmpty()) {
                associatedJoinColumns
                        // TODO 타입 변경
                        .forEach(joinColumn -> query.append(joinColumn.getJoinColumnName() + " " + "BIGINT" + ", "));

            }

        }

        definePrimaryKey(tableDefinition.getTableId(), query);

        query.append(");");
    }

    private static ColumnDefinition getColumnDefinition(TableDefinition tableDefinition,
                                                        JoinColumnDefinition joinColumn) {
        if (Objects.equals(tableDefinition.getTableId().getColumnName(), joinColumn.getJoinColumnName())) {
            return tableDefinition.getTableId().getColumnDefinition();
        }

        throw new IllegalArgumentException("Column not found");
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
