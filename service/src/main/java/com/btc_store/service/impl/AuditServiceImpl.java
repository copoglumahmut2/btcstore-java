package com.btc_store.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class AuditServiceImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
                var username = authentication.getPrincipal().toString();
                return Optional.ofNullable(username);

        }
        return Optional.empty();

    }
}
