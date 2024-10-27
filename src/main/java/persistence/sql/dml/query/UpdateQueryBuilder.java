package persistence.sql.dml.query;

import persistence.sql.Queryable;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private void columnClause(StringBuilder query, Map<String, Object> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }

        query.append(" SET ");
        String columnClause = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .reduce((column1, column2) -> column1 + ", " + column2).orElse("");
        query.append(columnClause);
    }

    public String build(Object entity) {
        final TableDefinition tableDefinition = new TableDefinition(entity.getClass());
        final Serializable idValue = tableDefinition.getIdValue(entity);

        final StringBuilder query = new StringBuilder("UPDATE ").append(tableDefinition.getTableName());
        columnClause(
                query,
                tableDefinition.withoutIdColumns().stream()
                        .collect(
                                Collectors.toMap(
                                        Queryable::getColumnName,
                                        column -> column.hasValue(entity) ? column.getValueWithQuoted(entity) : "null",
                                        (value1, value2) -> value2,
                                        LinkedHashMap::new
                                )
                        )

        );

        query.append(" WHERE ");
        query.append(tableDefinition.getTableId().getColumnName()).append(" = ");
        query.append(idValue);
        query.append(";");

        return query.toString();
    }

    public String build(Object parent, Object child, Serializable parentId, Serializable childId) {
        final TableDefinition parentTableDefinition = new TableDefinition(parent.getClass());
        final TableDefinition childTableDefinition = new TableDefinition(child.getClass());
        final TableAssociationDefinition childAssociationDefinition = parentTableDefinition.getAssociations().stream()
                .filter(association -> association.getAssociatedEntityClass().equals(child.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Association not found"));

        final StringBuilder query = new StringBuilder("UPDATE ").append(childAssociationDefinition.getTableName());
        columnClause(
                query,
                Map.of(
                        childAssociationDefinition.getJoinColumnName(),
                        parentId
                )
        );

        query.append(" WHERE ");
        query.append(childTableDefinition.getTableId().getColumnName()).append(" = ");
        query.append(childId);
        query.append(";");
        return query.toString();
    }
}
