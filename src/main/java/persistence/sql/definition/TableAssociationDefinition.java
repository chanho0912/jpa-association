package persistence.sql.definition;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;

public class TableAssociationDefinition {
    private final TableDefinition associatedTableDefinition;
    private final JoinColumnDefinition joinColumnDefinition;
    private final FetchType fetchType;
    private final String fieldName;

    public TableAssociationDefinition(Class<?> associatedEntityClass,
                                      Field field) {
        this.associatedTableDefinition = new TableDefinition(associatedEntityClass);
        this.joinColumnDefinition = new JoinColumnDefinition(field);
        this.fieldName = field.getName();
        this.fetchType = getFetchType(field);
    }

    private static FetchType getFetchType(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).fetch();
        }

        if (field.isAnnotationPresent(ManyToMany.class)) {
            return field.getAnnotation(ManyToMany.class).fetch();
        }

        return FetchType.EAGER;
    }

    public TableDefinition getAssociatedTableDefinition() {
        return associatedTableDefinition;
    }

    public Class<?> getAssociatedEntityClass() {
        return associatedTableDefinition.getEntityClass();
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
