package persistence.sql.definition;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;

public class JoinColumnDefinition {

    private final JoinColumn joinColumn;

    public JoinColumnDefinition(Field field) {
        this.joinColumn = field.getAnnotation(JoinColumn.class);
    }

    public String getJoinColumnName() {
        return joinColumn.name();
    }

}
