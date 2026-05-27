package me.nghlong3004.iom.api.application.port.out;

import me.nghlong3004.iom.api.domain.conversation.ConversationContext;

/**
 * Port for storing and retrieving per-user conversation context. Implementations can use in-memory
 * storage (dev), Redis (staging/prod), or database.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
public interface ConversationContextStore {

  /**
   * Retrieves the conversation context for the given key. Creates a new context if none exists.
   *
   * @param conversationKey unique key, typically {@code "{channel}:{externalUserId}"}
   * @return the conversation context (never null)
   */
  ConversationContext get(String conversationKey);

  /**
   * Persists the conversation context.
   *
   * @param context the context to save
   */
  void save(ConversationContext context);
}
