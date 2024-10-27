package persistence.sql.dml.query;

import org.junit.jupiter.api.Test;
import domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByIdQueryBuilderTest {
    @Test
    void testSelectById() {
        final String query = new SelectByIdQueryBuilder().build(Person.class, 1L);

        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1;");
    }

}
