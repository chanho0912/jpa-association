package persistence.sql.definition;

import jakarta.persistence.JoinColumn;
import persistence.sql.Queryable;

import java.lang.reflect.Field;
import java.util.List;

public class TableCollectionDefinition {
    private final TableDefinition tableDefinition;
    private final JoinColumn joinColumn;
    private final Class<?> entityClass;
    private final String fieldName;

    public TableCollectionDefinition(Class<?> entityClass,
                                     Field field) {
        this.tableDefinition = new TableDefinition(entityClass);
        this.joinColumn = getJoinColumn(tableDefinition);
        this.entityClass = entityClass;
        this.fieldName = field.getName();
    }

    private JoinColumn getJoinColumn(TableDefinition tableDefinition) {
        final List<JoinColumn> joinColumns = tableDefinition.withoutIdColumns().stream()
                .filter(Queryable::isJoinColumn)
                .map(Queryable::getJoinColumn)
                .toList();

        if (joinColumns.size() != 1) {
            throw new IllegalArgumentException("Collection must have exactly one join column");
        }

        return joinColumns.get(0);
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getFieldName() {
        return fieldName;
    }
}
