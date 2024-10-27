package persistence.sql.definition;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.sql.Queryable;

import java.lang.reflect.Field;
import java.util.List;

public class TableAssociationDefinition {
    private final TableDefinition associatedTableDefinition;
    private final JoinColumnDefinition joinColumnDefinition;
    private final FetchType fetchType;
    private final Class<?> associatedEntityClass;
    private final String fieldName;
    private final String JoinColumnName;

    public TableAssociationDefinition(Class<?> associatedEntityClass,
                                      Field field, String joinColumnName) {
        this.associatedTableDefinition = new TableDefinition(associatedEntityClass);
        this.joinColumnDefinition = new JoinColumnDefinition(field);
        this.associatedEntityClass = associatedEntityClass;
        this.fieldName = field.getName();
        this.fetchType = field.isAnnotationPresent(OneToMany.class) ?
                field.getAnnotation(OneToMany.class).fetch() : null;
        this.JoinColumnName = joinColumnName;
    }

    private JoinColumn getJoinColumn(TableDefinition tableDefinition) {
        final List<JoinColumn> joinColumns = tableDefinition.withoutIdColumns().stream()
                .filter(Queryable::hasJoinColumn)
                .map(Queryable::getJoinColumn)
                .toList();

        if (joinColumns.size() > 1) {
            throw new IllegalArgumentException("Collection must have one join column");
        }

        if (joinColumns.isEmpty()) {
            return null;
        }

        return joinColumns.get(0);
    }

    public TableDefinition getAssociatedTableDefinition() {
        return associatedTableDefinition;
    }

    public Class<?> getAssociatedEntityClass() {
        return associatedEntityClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTableName() {
        return associatedTableDefinition.getTableName();
    }

    public boolean isFetchEager() {
        return fetchType == FetchType.EAGER;
    }

    public String getJoinColumnName() {
        return joinColumnDefinition.getJoinColumnName();
    }
}
