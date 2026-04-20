package com.marketplace.ceara.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    CLIENT,
    PROVIDER;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
