package com.aecs.eyenotes.core.dto;

public abstract class TenantBaseDto<T> extends GenericBaseDto<T> {
    private T tenantId;

    public T getTenantId() {
        return tenantId;
    }

    public void setTenantId(T tenantId) {
        this.tenantId = tenantId;
    }
}