package persistence.sql.dml.query;

import persistence.sql.AliasUtils;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CustomSelectQueryBuilder {

    private final TableDefinition tableDefinition;
    private final List<String> columns = new ArrayList<>();

    private TableAssociationDefinition joinTableDefinition;
    private final List<String> joinTableColumns = new ArrayList<>();

    public CustomSelectQueryBuilder(Class<?> entityClass) {
        final TableDefinition tableDefinition = new TableDefinition(entityClass);
        this.tableDefinition = tableDefinition;
        tableDefinition.withIdColumns().forEach(column -> {
                    columns.add(column.getColumnName());
                }
        );
    }

    public CustomSelectQueryBuilder join(TableAssociationDefinition tableAssociationDefinition) {
        final TableDefinition joinTableDefinition = tableAssociationDefinition.getAssociatedTableDefinition();
        this.joinTableDefinition = tableAssociationDefinition;
        joinTableDefinition.withIdColumns().forEach(column -> {
                    joinTableColumns.add(column.getColumnName());
                }
        );
        return this;
    }

    public String build() {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(columnsClause());
        query.append(" FROM ");
        query.append(tableDefinition.getTableName());
        if (joinTableDefinition != null) {
            query.append(" LEFT JOIN ");
            query.append(joinTableDefinition.getTableName());
            query.append(" ON ");
            query.append(joinTableDefinition.getTableName());
            query.append(".");
            query.append(joinTableDefinition.getJoinColumnName());
            query.append(" = ");
            query.append(tableDefinition.getTableName());
            query.append(".");
            query.append(tableDefinition.getTableId().getColumnName());
        }
        query.append(";");
        return query.toString();
    }

    private String columnsClause() {
        final StringJoiner joiner = new StringJoiner(", ");

        columns.forEach(column -> {
            final String aliased = AliasUtils.alias(tableDefinition.getTableName(), column);
            joiner.add(tableDefinition.getTableName() + "." + column + " AS " + aliased);
        });

        joinTableColumns.forEach(column -> {
            final String aliased = AliasUtils.alias(joinTableDefinition.getTableName(), column);
            joiner.add(joinTableDefinition.getTableName() + "." + column + " AS " + aliased);
        });

        return joiner.toString();
    }

}
