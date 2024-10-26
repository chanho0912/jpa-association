package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CustomSelectQueryBuilder {

    private final TableDefinition tableDefinition;
    private final List<String> columns = new ArrayList<>();

    private TableDefinition joinTableDefinition;
    private final List<String> joinTableColumns = new ArrayList<>();

    public CustomSelectQueryBuilder(Class<?> entityClass) {
        final TableDefinition tableDefinition = new TableDefinition(entityClass);
        this.tableDefinition = tableDefinition;
        tableDefinition.withIdColumns().forEach(column -> {
                    columns.add(column.getColumnName());
                }
        );
    }

    public CustomSelectQueryBuilder join(Class<?> joinEntityClass) {
        final TableDefinition joinTableDefinition = new TableDefinition(joinEntityClass);
        this.joinTableDefinition = joinTableDefinition;
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
        query.append(tableDefinition.getTableName()).append(" ").append(
                AliasRule.getTableAlias(tableDefinition)
        );
        if (joinTableDefinition != null) {
            query.append(" LEFT JOIN ");
            query.append(joinTableDefinition.getTableName()).append(" ").append(
                    AliasRule.getJoinTableAlias(joinTableDefinition)
            );
            query.append(" ON ");
            query.append(AliasRule.getJoinTableAlias(joinTableDefinition));
            query.append(".");
            query.append(joinTableDefinition.getTableId().getColumnName());
            query.append(" = ");
            query.append(AliasRule.getTableAlias(tableDefinition));
            query.append(".");
            query.append(tableDefinition.getTableId().getColumnName());
        }
        query.append(";");
        return query.toString();
    }

    private String columnsClause() {
        final StringJoiner joiner = new StringJoiner(", ");

        columns.forEach(column -> {
            String aliased = AliasRule.getTableAlias(tableDefinition) + "." + column;
            aliased += " AS " + AliasRule.getColumnAlias(tableDefinition, column);

            joiner.add(aliased);
        });

        joinTableColumns.forEach(column -> {
            String aliased = AliasRule.getJoinTableAlias(joinTableDefinition) + "." + column;
            aliased += " AS " + AliasRule.getJoinColumnAlias(joinTableDefinition, column);

            joiner.add(aliased);
        });

        return joiner.toString();
    }

}
