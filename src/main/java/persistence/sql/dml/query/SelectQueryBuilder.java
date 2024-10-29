package persistence.sql.dml.query;

import common.AliasRule;
import persistence.sql.definition.EntityTableMapper;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SelectQueryBuilder implements BaseQueryBuilder {

    private final TableDefinition tableDefinition;
    private final List<String> columns = new ArrayList<>();

    private TableDefinition joinTableDefinition;
    private final List<String> joinTableColumns = new ArrayList<>();

    public SelectQueryBuilder(Class<?> entityClass) {
        final TableDefinition tableDefinition = new TableDefinition(entityClass);
        this.tableDefinition = tableDefinition;
        tableDefinition.getColumns().forEach(column -> {
                    columns.add(column.getDatabaseColumnName());
                }
        );
    }

    public SelectQueryBuilder join(TableAssociationDefinition tableAssociationDefinition) {
        final TableDefinition joinTableDefinition = tableAssociationDefinition.getAssociatedTableDefinition();
        this.joinTableDefinition = new TableDefinition(joinTableDefinition.getEntityClass());

        this.joinTableDefinition.getColumns().forEach(column -> {
                    joinTableColumns.add(column.getDatabaseColumnName());
                }
        );
        return this;
    }

    public String build(Serializable id) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(columnsClause())
                .append(" FROM ")
                .append(tableDefinition.getTableName());
        if (joinTableDefinition != null) {
            query.append(" LEFT JOIN ")
                    .append(joinTableDefinition.getTableName())
                    .append(" ON ")
                    .append(joinTableDefinition.getTableName())
                    .append(".")
                    .append(tableDefinition.getJoinColumnName(joinTableDefinition.getEntityClass()))
                    .append(" = ")
                    .append(tableDefinition.getTableName())
                    .append(".")
                    .append(tableDefinition.getIdColumnName());
        }
        whereClause(query, id);
        return query.toString();
    }

    private String columnsClause() {
        final StringJoiner joiner = new StringJoiner(", ");

        columns.forEach(column -> {
            final String aliased = AliasRule.buildWith(tableDefinition.getTableName(), column);
            joiner.add(tableDefinition.getTableName() + "." + column + " AS " + aliased);
        });

        joinTableColumns.forEach(column -> {
            final String aliased = AliasRule.buildWith(joinTableDefinition.getTableName(), column);
            joiner.add(joinTableDefinition.getTableName() + "." + column + " AS " + aliased);
        });

        return joiner.toString();
    }

    private void whereClause(StringBuilder selectQuery, Serializable id) {
        selectQuery
                .append(" WHERE ")
                .append(tableDefinition.getTableName())
                .append(".")
                .append(tableDefinition.getIdColumnName())
                .append(" = ")
                .append(getQuoted(id)).append(";");
    }

}
