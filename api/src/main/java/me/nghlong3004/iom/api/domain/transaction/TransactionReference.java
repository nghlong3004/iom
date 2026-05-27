package me.nghlong3004.iom.api.domain.transaction;

import java.util.Objects;

/**
 * Sealed hierarchy for referencing a specific transaction in delete/update/undo operations.
 *
 * <p>Three strategies for identifying a transaction:
 *
 * <ul>
 *   <li>{@link Latest} — the most recently recorded transaction
 *   <li>{@link ByIndex} — by position in the last viewed history list (1-based)
 *   <li>{@link ByMatch} — by content description, resolved via LLM fuzzy matching
 * </ul>
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
public sealed interface TransactionReference {

  /** Reference the most recently recorded transaction. */
  record Latest() implements TransactionReference {}

  /** Reference by position index in the last viewed transaction list (1-based). */
  record ByIndex(int index) implements TransactionReference {
    public ByIndex {
      if (index < 1) {
        throw new IllegalArgumentException("index must be >= 1");
      }
    }
  }

  /** Reference by content description — resolved via LLM fuzzy matching. */
  record ByMatch(String description) implements TransactionReference {
    public ByMatch {
      Objects.requireNonNull(description, "description is required");
      if (description.isBlank()) {
        throw new IllegalArgumentException("description must not be blank");
      }
    }
  }
}
