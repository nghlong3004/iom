package me.nghlong3004.iom.api.domain.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/26/2026
 */
@DisplayName("ParsedTransaction Unit Tests")
class ParsedTransactionTest {

  @Test
  @DisplayName("Should reject null type")
  void constructor_NullType_ThrowsException() {
    assertThatThrownBy(() -> new ParsedTransaction(null, 1L, Currency.VND, Category.OTHER, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("type must not be null");
  }

  @Test
  @DisplayName("Should reject null currency")
  void constructor_NullCurrency_ThrowsException() {
    assertThatThrownBy(
            () -> new ParsedTransaction(TransactionType.EXPENSE, 1L, null, Category.OTHER, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("currency must not be null");
  }

  @Test
  @DisplayName("Should reject null category")
  void constructor_NullCategory_ThrowsException() {
    assertThatThrownBy(
            () -> new ParsedTransaction(TransactionType.EXPENSE, 1L, Currency.VND, null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("category must not be null");
  }

  @Test
  @DisplayName("Should reject non-positive amount")
  void constructor_NonPositiveAmount_ThrowsException() {
    assertThatThrownBy(
            () ->
                new ParsedTransaction(
                    TransactionType.EXPENSE, 0L, Currency.VND, Category.OTHER, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("amount must be positive");
  }

  @Test
  @DisplayName("Should create record with valid input")
  void constructor_ValidInput_CreatesRecord() {
    var parsed =
        new ParsedTransaction(
            TransactionType.EXPENSE, 30000L, Currency.VND, Category.FOOD, "an sang", null);

    assertThat(parsed.type()).isEqualTo(TransactionType.EXPENSE);
    assertThat(parsed.amount()).isEqualTo(30000L);
    assertThat(parsed.currency()).isEqualTo(Currency.VND);
  }

  @Test
  @DisplayName("Should reject negative amount")
  void constructor_NegativeAmount_ThrowsException() {
    assertThatThrownBy(
            () ->
                new ParsedTransaction(
                    TransactionType.EXPENSE, -1L, Currency.VND, Category.OTHER, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("amount must be positive");
  }

  @Test
  @DisplayName("Should allow null note and null occurredAt")
  void constructor_NullNoteAndDate_AllowsNulls() {
    var parsed =
        new ParsedTransaction(TransactionType.INCOME, 1L, Currency.USD, Category.OTHER, null, null);

    assertThat(parsed.note()).isNull();
    assertThat(parsed.occurredAt()).isNull();
  }
}
