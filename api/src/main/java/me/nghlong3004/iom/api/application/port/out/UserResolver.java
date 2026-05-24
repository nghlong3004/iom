package me.nghlong3004.iom.api.application.port.out;

import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import me.nghlong3004.iom.api.domain.user.AppUser;

/**
 * Resolves an incoming message to an application user. Abstracts away platform-specific user lookup
 * and auto-provisioning.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
public interface UserResolver {

  AppUser resolve(IncomingMessage message);
}
