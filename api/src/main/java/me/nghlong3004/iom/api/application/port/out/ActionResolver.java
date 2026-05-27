package me.nghlong3004.iom.api.application.port.out;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;

/**
 * Resolves natural-language text into a {@link TransactionAction}. Implementations form a Chain of
 * Responsibility: deterministic keyword matching first, LLM fallback second.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
public interface ActionResolver {

  /**
   * Attempts to resolve a transaction management action from the given text.
   *
   * @param normalizedText the user's message text
   * @return the resolved action, or empty if this resolver cannot handle the text
   */
  Optional<TransactionAction> resolve(String normalizedText);
}
