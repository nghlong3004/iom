package me.nghlong3004.iom.api.repository;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.user.ExternalAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Long> {

  Optional<ExternalAccount> findByPlatformAndExternalUserId(
      MessageChannel platform, String externalUserId);
}
