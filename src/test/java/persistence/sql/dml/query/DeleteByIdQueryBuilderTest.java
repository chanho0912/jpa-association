package persistence.sql.dml.query;

import org.junit.jupiter.api.Test;
import domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteByIdQueryBuilderTest {
    @Test
    void testDeleteById() {
        Person person = new Person(1L, "John", 30, "", 1);
        final String query = new DeleteByIdQueryBuilder().build(person);

        assertThat(query).isEqualTo("DELETE FROM users WHERE id = 1;");
    }
}
