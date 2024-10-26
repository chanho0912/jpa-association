package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

public class AliasRule {
    private static final String UNDERSCORE = "_";
    private static final Integer TABLE_PREFIX_LENGTH = 3;
    private static final String TABLE_PREFIX = "t";
    private static final String JOIN_TABLE_PREFIX = "jt";

    private AliasRule() {
    }

    public static String getTableAlias(TableDefinition tableDefinition) {
        return TABLE_PREFIX + UNDERSCORE + tableDefinition.getTableName().substring(0, TABLE_PREFIX_LENGTH).toLowerCase();
    }

    public static String getColumnAlias(TableDefinition tableDefinition, String columnName) {
        return getTableAlias(tableDefinition) + UNDERSCORE + columnName;
    }

    public static String getJoinTableAlias(TableDefinition tableDefinition) {
        return JOIN_TABLE_PREFIX + UNDERSCORE + tableDefinition.getTableName().substring(0, TABLE_PREFIX_LENGTH).toLowerCase();
    }

    public static String getJoinColumnAlias(TableDefinition tableDefinition, String columnName) {
        return getJoinTableAlias(tableDefinition) + UNDERSCORE + columnName;
    }
}
