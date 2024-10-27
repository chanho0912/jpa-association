package persistence.sql.dml.query;

public class AliasRule {
    private static final String UNDERSCORE = "_";
    private static final Integer TABLE_PREFIX_LENGTH = 3;
    private static final String TABLE_PREFIX = "t";
    private static final String JOIN_TABLE_PREFIX = "jt";

    private AliasRule() {
    }

    public static String getTableAlias(String tableName) {
        return TABLE_PREFIX + UNDERSCORE + tableName.substring(0, TABLE_PREFIX_LENGTH).toLowerCase();
    }

    public static String getColumnAlias(String tableName, String columnName) {
        return getTableAlias(tableName) + UNDERSCORE + columnName;
    }

    public static String getJoinTableAlias(String tableName) {
        return JOIN_TABLE_PREFIX + UNDERSCORE + tableName.substring(0, TABLE_PREFIX_LENGTH).toLowerCase();
    }

    public static String getJoinColumnAlias(String tableName, String columnName) {
        return getJoinTableAlias(tableName) + UNDERSCORE + columnName;
    }
}
