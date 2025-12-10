package org.acme.web.config;

import java.util.Optional;

import org.acme.web.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    @Override
    @NonNull
    @SuppressWarnings("null")
    public Optional<User> getCurrentAuditor() {
        // For dev/localhost, return empty Optional (audit fields will be null)
        // In a real app, this would get the current authenticated user from
        // SecurityContext
        return Optional.empty();
    }
}
