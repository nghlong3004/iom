package me.nghlong3004.iom.api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import me.nghlong3004.iom.api.application.port.out.ConversationContextStore;
import me.nghlong3004.iom.api.domain.conversation.ConversationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of {@link ConversationContextStore} using {@link ConcurrentHashMap}.
 * Contexts expire after 30 minutes of inactivity and are cleaned up periodically.
 *
 * <p>Suitable for development and single-instance deployments. For production clusters, swap with a
 * Redis-backed implementation.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@Slf4j
@Component
public class InMemoryConversationContextStore implements ConversationContextStore {

  private static final Duration TTL = Duration.ofMinutes(30);

  private final ConcurrentHashMap<String, ConversationContext> store = new ConcurrentHashMap<>();

  @Override
  public ConversationContext get(String conversationKey) {
    return store.computeIfAbsent(conversationKey, ConversationContext::new);
  }

  @Override
  public void save(ConversationContext context) {
    store.put(context.getConversationKey(), context);
  }

  /** Removes expired contexts every 5 minutes. */
  @Scheduled(fixedDelay = 300_000)
  void cleanupExpired() {
    var cutoff = Instant.now().minus(TTL);
    var removed =
        store.entrySet().removeIf(entry -> entry.getValue().getLastActivityAt().isBefore(cutoff));
    if (removed) {
      log.debug("Cleaned up expired conversation contexts.");
    }
  }
}
