package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

import java.util.StringJoiner;

public class SelectByIdQueryBuilder {
    public String build(Class<?> entityClass, Object id) {
        final StringBuilder query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("SELECT ");
        StringJoiner columns = new StringJoiner(", ");

        tableDefinition.withIdColumns().forEach(column -> {
            String columnName = column.getColumnName();
            columns.add(tableDefinition.getTableName() + "." + columnName);
        });


        query.append(columns);
        query.append(" FROM ").append(tableDefinition.getTableName());

        whereClause(query, tableDefinition, id);
        return query.toString();
    }

    private void whereClause(StringBuilder selectQuery, TableDefinition tableDefinition, Object id) {
        selectQuery.append(" WHERE ");
        selectQuery.append(tableDefinition.getTableName())
                .append(".")
                .append(tableDefinition.getTableId().getColumnName())
                .append(" = ");

        if (id instanceof String) {
            selectQuery.append("'").append(id).append("';");
            return;
        }

        selectQuery.append(id).append(";");
    }

}
