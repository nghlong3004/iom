package me.nghlong3004.iom.api.domain.transaction;

/**
 * Value object describing which fields to update on a transaction. Null fields mean "don't change".
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
public record UpdateFields(
    Long amount, Category category, String note, TransactionType type) {

  /** Returns {@code true} if at least one field has a non-null value. */
  public boolean hasChanges() {
    return amount != null || category != null || note != null || type != null;
  }
}
