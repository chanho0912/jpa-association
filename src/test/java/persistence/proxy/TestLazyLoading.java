package persistence.proxy;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderItem;
import jdbc.EagerFetchRowMapper;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityLazyLoader;
import persistence.sql.H2Dialect;
import persistence.sql.SqlType;
import persistence.sql.ddl.query.CreateTableQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.EntityTableMapper;
import persistence.sql.dml.query.SelectQueryBuilder;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TestLazyLoading {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(TestLazyLoading.class);

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        String query1 = new CreateTableQueryBuilder(new H2Dialect(), Order.class, List.of()).build();
        String query2 = new CreateTableQueryBuilder(new H2Dialect(), OrderItem.class, Collections.singletonList(new ColumnDefinitionAware() {
            @Override
            public String getDatabaseColumnName() {
                return "order_id";
            }

            @Override
            public String getEntityFieldName() {
                return "id";
            }

            @Override
            public boolean isNullable() {
                return true;
            }

            @Override
            public int getLength() {
                return 0;
            }

            @Override
            public SqlType getSqlType() {
                return SqlType.BIGINT;
            }

            @Override
            public boolean isPrimaryKey() {
                return false;
            }
        })).build();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(query1);
        jdbcTemplate.execute(query2);
    }

    @AfterEach
    void tearDown() throws SQLException {
        String query1 = new DropQueryBuilder(Order.class).build();
        String query2 = new DropQueryBuilder(OrderItem.class).build();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(query1);
        jdbcTemplate.execute(query2);
        server.stop();
    }

    @Test
    void testLazyLoading() {
        jdbcTemplate.execute("INSERT INTO orders (orderNumber) VALUES ('order_number')");
        jdbcTemplate.execute("INSERT INTO order_items (product, quantity, order_id) VALUES ('product1', 1, 1)");
        jdbcTemplate.execute("INSERT INTO order_items (product, quantity, order_id) VALUES ('product2', 2, 1)");

        List orderItems = (List) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{List.class},
                new PersistentList(new Order(1L, "order_number"), OrderItem.class, lazyLoader())
        );
        logger.info("Proxy Instance Created");

        logger.info("Call order item size");
        logger.info("{}", orderItems.size());
    }

    private static EntityLazyLoader lazyLoader() {
        return new EntityLazyLoader() {
            @Override
            public <T> Collection<T> loadLazyCollection(Class<T> targetClass, EntityTableMapper ownerTableMapper) {
                final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(targetClass);
                final String joinColumnName = ownerTableMapper.getJoinColumnName(targetClass);
                final Object value = ownerTableMapper.getValue(joinColumnName);

                final String query = queryBuilder
                        .where(joinColumnName, value.toString())
                        .build();

                return jdbcTemplate.query(query,
                        new EagerFetchRowMapper<>(targetClass)
                );
            }
        };
    }
}
