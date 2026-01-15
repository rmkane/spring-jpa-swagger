package org.acme.web.repository;

import java.util.Optional;

import org.acme.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/**
 * Repository for User entities. Note: User has no relationships, so no JOIN
 * FETCH is needed. User is the audit source (createdBy/updatedBy), not an
 * audited entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(@NonNull String username);

    Optional<User> findByEmail(@NonNull String email);
}
