package com.aecs.eyenotes.core.repositories;

import com.aecs.eyenotes.core.entities.GenericBaseEntity;
import com.aecs.eyenotes.core.entities.TenantAuditEntity;
import com.aecs.eyenotes.core.response.PagedResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TenantRepository<T, TEntity extends GenericBaseEntity<T>> extends BaseRepository<T, TEntity> implements ITenantRepository<T, TEntity> {
    protected TenantRepository(Connection connection, String tableName) {
        super(connection, tableName);
    }

    @Override
    public List<TEntity> getAll(T tenantId) throws SQLException {
        String sql = "SELECT * FROM " + tableName + 
                     " WHERE tenant_id = ? AND (is_deleted = false OR is_deleted IS NULL)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            
            List<TEntity> entities = new ArrayList<>();
            while (rs.next()) {
                mapResultSetToEntity(rs).ifPresent(entities::add);
            }
            return entities;
        }
    }

    @Override
    public List<TEntity> find(T tenantId, String filter) throws SQLException {
        String sql = "SELECT * FROM " + tableName + 
                     " WHERE tenant_id = ? AND (is_deleted = false OR is_deleted IS NULL) AND " + filter;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            
            List<TEntity> entities = new ArrayList<>();
            while (rs.next()) {
                mapResultSetToEntity(rs).ifPresent(entities::add);
            }
            return entities;
        }
    }

    @Override
    public PagedResponse<TEntity> getPaged(T tenantId, int pageIndex, int pageSize, 
                                         String filter, String sort) throws SQLException {
        long count = count(tenantId, filter);
        
        String sql;
        if (sort != null && !sort.isEmpty()) {
            sql = "SELECT * FROM " + tableName + 
                  " WHERE tenant_id = ? AND (is_deleted = false OR is_deleted IS NULL)" +
                  (filter != null ? " AND " + filter : "") + 
                  " ORDER BY " + sort + 
                  " LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT * FROM " + tableName + 
                  " WHERE tenant_id = ? AND (is_deleted = false OR is_deleted IS NULL)" +
                  (filter != null ? " AND " + filter : "") + 
                  " ORDER BY id" +
                  " LIMIT ? OFFSET ?";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tenantId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (pageIndex - 1) * pageSize);
            
            ResultSet rs = stmt.executeQuery();
            
            List<TEntity> items = new ArrayList<>();
            while (rs.next()) {
                mapResultSetToEntity(rs).ifPresent(items::add);
            }
            
            return new PagedResponse<>(count, items);
        }
    }

    @Override
    public long count(T tenantId, String filter) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + 
                    " WHERE tenant_id = ? AND (is_deleted = false OR is_deleted IS NULL)" +
                    (filter != null ? " AND " + filter : "");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }

    @Override
    public boolean any(T tenantId, String filter) throws SQLException {
        return count(tenantId, filter) > 0;
    }

    @Override
    public void add(T tenantId, TEntity entity) throws SQLException {
        entity.setTenantId(tenantId);
        if (entity instanceof AuditEntity) {
            ((AuditEntity) entity).setCreatedAt(java.time.OffsetDateTime.now());
        }
        // This needs to be implemented based on the specific entity structure
        // For now, just throwing as it needs to be implemented in subclasses
        throw new UnsupportedOperationException("Not implemented in base class");
    }

    @Override
    public void addRange(T tenantId, Iterable<TEntity> entities) throws SQLException {
        for (TEntity entity : entities) {
            add(tenantId, entity);
        }
    }
}