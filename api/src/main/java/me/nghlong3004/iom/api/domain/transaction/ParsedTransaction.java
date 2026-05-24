package me.nghlong3004.iom.api.domain.transaction;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable value object representing the structured output from {@link
 * me.nghlong3004.iom.api.application.port.out.MessageInterpreter}.
 *
 * <p>Amounts are stored in the smallest unit of the detected currency (dong for VND, cents for
 * USD/EUR).
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
public record ParsedTransaction(
    TransactionType type,
    long amount,
    Currency currency,
    Category category,
    String note,
    LocalDate occurredAt) {

  public ParsedTransaction {
    Objects.requireNonNull(type, "type must not be null");
    Objects.requireNonNull(currency, "currency must not be null");
    Objects.requireNonNull(category, "category must not be null");
    if (amount <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
  }
}
