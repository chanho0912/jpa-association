package persistence.sql.definition;

import jakarta.persistence.JoinColumn;
import persistence.sql.Dialect;
import persistence.sql.Queryable;

import java.lang.reflect.Field;

public class TableAssociatedColumn implements Queryable {
    private final ColumnDefinition columnDefinition;

    public TableAssociatedColumn(Field field) {
        this.columnDefinition = new ColumnDefinition(field);
    }

    @Override
    public void applyToCreateTableQuery(StringBuilder query, Dialect dialect) {
        final String type = dialect.translateType(columnDefinition);
        query.append(columnDefinition.getColumnName()).append(" ").append(type);
        query.append(", ");
    }

    @Override
    public boolean hasValue(Object entity) {
        return false;
    }

    @Override
    public String getValueWithQuoted(Object entity) {
        return "";
    }

    @Override
    public Object getValue(Object entity) {
        return null;
    }

    @Override
    public String getColumnName() {
        return "";
    }

    @Override
    public String getDeclaredName() {
        return "";
    }

    @Override
    public boolean hasJoinColumn() {
        return false;
    }

    @Override
    public JoinColumn getJoinColumn() {
        return null;
    }
}
