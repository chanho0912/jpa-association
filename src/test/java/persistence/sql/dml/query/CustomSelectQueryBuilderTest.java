package persistence.sql.dml.query;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.Test;
import persistence.sql.definition.TableDefinition;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSelectQueryBuilderTest {

    @Test
    void testSelectSingleTable() {
        Order order = new Order("order_number", new ArrayList<>());
        TableDefinition tableDefinition = new TableDefinition(order.getClass());

        String selectQuery = new CustomSelectQueryBuilder(tableDefinition).build();
        assertThat(selectQuery).isEqualTo("SELECT t_ord.id AS orders_id, t_ord.orderNumber AS orders_orderNumber FROM orders t_ord;");
    }

    @Test
    void testSelectSingleTableWithJoin() {
        Order order = new Order("order_number", new ArrayList<>());
        TableDefinition orderTableDefinition = new TableDefinition(order.getClass());
        OrderItem orderItem = new OrderItem("item_name", 1);
        TableDefinition orderItemTableDefinition = new TableDefinition(orderItem.getClass());

        String selectQuery = new CustomSelectQueryBuilder(orderTableDefinition)
                .join(orderItemTableDefinition)
                .build();

        assertThat(selectQuery).isEqualTo("SELECT t_ord.id AS orders_id, t_ord.orderNumber AS orders_orderNumber, " +
                "jt_ord.id AS order_items_id, jt_ord.product AS order_items_product, jt_ord.quantity AS order_items_quantity " +
                "FROM orders t_ord LEFT JOIN order_items jt_ord ON order_items.id = orders.id;");
    }
}
