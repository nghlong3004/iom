package me.nghlong3004.iom.api.repository;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByEmail(String email);
}
