package persistence.sql.dml.query;

import org.jetbrains.annotations.NotNull;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CustomSelectQueryBuilder {

    private static final String DEFAULT_TABLE_ALIAS = "t";
    private static final String DEFAULT_JOIN_TABLE_ALIAS = "jt";

    private final TableDefinition tableDefinition;
    private final List<String> columns = new ArrayList<>();
    private final Map<String, String> columnAliases = new HashMap<>();
    private final String tableAlias;

    private TableDefinition joinTableDefinition;
    private String joinTableAlias;
    private final List<String> joinTableColumns = new ArrayList<>();
    private final Map<String, String> joinTableColumnAliases = new HashMap<>();

    public CustomSelectQueryBuilder(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
        this.tableAlias = tableDefinition.getTableName().substring(0, 3).toLowerCase();
        tableDefinition.withIdColumns().forEach(column -> {
                    columns.add(column.getColumnName());
                    columnAliases.put(column.getColumnName(), getAlias(tableDefinition, column));
                }
        );
    }

    public CustomSelectQueryBuilder join(TableDefinition joinTableDefinition) {
        this.joinTableDefinition = joinTableDefinition;
        this.joinTableAlias = joinTableDefinition.getTableName().substring(0, 3).toLowerCase();
        joinTableDefinition.withIdColumns().forEach(column -> {
                    joinTableColumns.add(column.getColumnName());
                    joinTableColumnAliases.put(column.getColumnName(), getAlias(joinTableDefinition, column));
                }
        );
        return this;
    }

    @NotNull
    private String getAlias(TableDefinition tableDefinition, Queryable column) {
        return tableDefinition.getTableName() + "_" + column.getColumnName();
    }

    public String build() {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(columnsClause());
        query.append(" FROM ");
        query.append(tableDefinition.getTableName()).append(" ").append(getTableAlias());
        if (joinTableDefinition != null) {
            query.append(" LEFT JOIN ");
            query.append(joinTableDefinition.getTableName()).append(" ").append(getJoinTableAlias());
            query.append(" ON ");
            query.append(joinTableDefinition.getTableName());
            query.append(".");
            query.append(joinTableDefinition.getTableId().getColumnName());
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
            String aliased = getTableAlias() + "." + column;
            if (columnAliases.containsKey(column)) {
                aliased += " AS " + columnAliases.get(column);
            }

            joiner.add(aliased);
        });

        joinTableColumns.forEach(column -> {
            String aliased = getJoinTableAlias() + "." + column;
            if (joinTableColumnAliases.containsKey(column)) {
                aliased += " AS " + joinTableColumnAliases.get(column);
            }

            joiner.add(aliased);
        });

        return joiner.toString();
    }

    private String getTableAlias() {
        return DEFAULT_TABLE_ALIAS + "_" + tableAlias;
    }

    private String getJoinTableAlias() {
        return DEFAULT_JOIN_TABLE_ALIAS + "_" + joinTableAlias;
    }
}
