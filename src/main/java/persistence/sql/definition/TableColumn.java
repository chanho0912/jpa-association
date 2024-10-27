package persistence.sql.definition;

import jakarta.persistence.JoinColumn;
import persistence.sql.Dialect;
import persistence.sql.Queryable;

import java.lang.reflect.Field;

public class TableColumn implements Queryable {
    private final ColumnDefinition columnDefinition;

    public TableColumn(Field field) {
        this.columnDefinition = new ColumnDefinition(field);
    }

    @Override
    public void applyToCreateTableQuery(StringBuilder query, Dialect dialect) {
        final String type = dialect.translateType(columnDefinition);
        query.append(columnDefinition.getColumnName()).append(" ").append(type);

        if (columnDefinition.isNotNullable()) {
            query.append(" NOT NULL");
        }

        query.append(", ");
    }

    @Override
    public boolean hasValue(Object entity) {
        return columnDefinition.hasValue(entity);
    }

    @Override
    public String getValueWithQuoted(Object entity) {
        final Object value = columnDefinition.getValue(entity);

        if (value instanceof String) {
            return "'" + value + "'";
        }

        return value.toString();
    }

    @Override
    public Object getValue(Object entity) {
        return columnDefinition.getValue(entity);
    }

    @Override
    public String getColumnName() {
        return columnDefinition.getColumnName();
    }

    @Override
    public String getDeclaredName() {
        return columnDefinition.getDeclaredName();
    }

    @Override
    public boolean isJoinColumn() {
        return columnDefinition.isJoinColumn();
    }

    @Override
    public JoinColumn getJoinColumn() {
        return columnDefinition.getJoinColumn();
    }

}
