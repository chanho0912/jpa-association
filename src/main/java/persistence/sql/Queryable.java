package persistence.sql;

import jakarta.persistence.JoinColumn;

public interface Queryable {

    void applyToCreateTableQuery(StringBuilder query, Dialect dialect);

    boolean hasValue(Object entity);

    String getValueWithQuoted(Object entity);

    Object getValue(Object entity);

    String getColumnName();

    String getDeclaredName();

    boolean hasJoinColumn();

    JoinColumn getJoinColumn();

}
