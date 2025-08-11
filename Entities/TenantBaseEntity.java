package com.aecs.eyenotes.core.entities;

import javax.persistence.Column;

public abstract class TenantBaseEntity<T> extends GenericBaseEntity<T> {
    @Column(name = "organization_id")
    private T tenantId;

    public T getTenantId() {
        return tenantId;
    }

    public void setTenantId(T tenantId) {
        this.tenantId = tenantId;
    }
}