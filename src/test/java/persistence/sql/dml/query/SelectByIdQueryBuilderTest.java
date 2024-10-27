package persistence.sql.dml.query;

import org.junit.jupiter.api.Test;
import domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByIdQueryBuilderTest {
    @Test
    void testSelectById() {
        final String query = new SelectByIdQueryBuilder().build(Person.class, 1L);

        assertThat(query).isEqualTo("SELECT t_use.id AS t_use_id, t_use.nick_name AS t_use_nick_name, t_use.old AS t_use_old, t_use.email AS t_use_email FROM users t_use WHERE t_use.id = 1;");
    }

}
