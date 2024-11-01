package persistence.entity;

import persistence.sql.definition.EntityTableMapper;

import java.util.Collection;

public interface EntityLazyLoader {
    Collection<?> loadLazyCollection(EntityTableMapper ownerTableMapper);
}
