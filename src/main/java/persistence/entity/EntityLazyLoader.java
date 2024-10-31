package persistence.entity;

import persistence.sql.definition.EntityTableMapper;

import java.util.Collection;

public interface EntityLazyLoader {
    public <T> Collection<T> loadLazyCollection(Class<T> targetClass, EntityTableMapper ownerTableMapper);
}
