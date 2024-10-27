package persistence.sql.dml.query;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSelectQueryBuilderTest {

    @Test
    void testSelectSingleTable() {
        Order order = new Order("order_number");
        String selectQuery = new CustomSelectQueryBuilder(order.getClass()).build();
        assertThat(selectQuery).isEqualTo("SELECT t_ord.order_id AS t_ord_order_id, t_ord.orderNumber AS t_ord_orderNumber FROM orders t_ord;");
    }
//
//    @Test
//    void testSelectSingleTableWithJoin() {
//        Order order = new Order("order_number");
//        OrderItem orderItem = new OrderItem("item_name", 1);
//
//        String selectQuery = new CustomSelectQueryBuilder(order.getClass())
//                .join(orderItem.getClass())
//                .build();
//
//        assertThat(selectQuery).isEqualTo("SELECT t_ord.order_id AS t_ord_order_id, t_ord.orderNumber AS t_ord_orderNumber, jt_ord.id AS jt_ord_id, jt_ord.product AS jt_ord_product, jt_ord.quantity AS jt_ord_quantity FROM orders t_ord LEFT JOIN order_items jt_ord ON jt_ord.id = t_ord.order_id;");
//    }
}
