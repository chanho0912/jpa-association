package persistence.sql.dml.query;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {
    @Entity
    private static class HasNullableColumnEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private Integer age;

        public HasNullableColumnEntity() {
        }

        public HasNullableColumnEntity(Long id, Integer age) {
            this.id = id;
            this.age = age;
        }

        public HasNullableColumnEntity(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    @Test
    @DisplayName("모든 필드에 대한 update 쿼리를 정상적으로 생성한다.")
    void shouldBuildUpdateQuery() {
        HasNullableColumnEntity hasNullableColumnEntity = new HasNullableColumnEntity(1L, "john_doe", 30);
        String query = new UpdateQueryBuilder().build(hasNullableColumnEntity);

        assertThat(query).contains("UPDATE HasNullableColumnEntity SET name = 'john_doe', age = 30 WHERE id = 1;");
    }

    @Test
    @DisplayName("nullable 필드가 있어도 update 쿼리를 정상적으로 생성한다.")
    void shouldBuildUpdateQueryWhenHasNullableColumns() {
        HasNullableColumnEntity hasNullableColumnEntity = new HasNullableColumnEntity(1L, 30);
        String query = new UpdateQueryBuilder().build(hasNullableColumnEntity);

        assertThat(query).contains("UPDATE HasNullableColumnEntity SET name = null, age = 30 WHERE id = 1;");
    }
}