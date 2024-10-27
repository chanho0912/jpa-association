package persistence.sql.definition;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.sql.Queryable;

import java.lang.reflect.Field;
import java.util.List;

public class TableAssociationDefinition {
    private final TableDefinition associatedTableDefinition;
    private final JoinColumn joinColumn;
    private final boolean isOneToMany;
    private final FetchType fetchType;
    private final Class<?> entityClass;
    private final String fieldName;

    public TableAssociationDefinition(Class<?> entityClass,
                                      Field field) {
        this.associatedTableDefinition = new TableDefinition(entityClass);
        this.joinColumn = getJoinColumn(associatedTableDefinition);
        this.entityClass = entityClass;
        this.fieldName = field.getName();
        this.isOneToMany = field.isAnnotationPresent(OneToMany.class);
        this.fetchType = field.isAnnotationPresent(OneToMany.class) ?
                field.getAnnotation(OneToMany.class).fetch() : null;
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

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getFieldName() {
        return fieldName;
    }
}
