package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.nghlong3004.iom.api.domain.transaction.Currency;
import me.nghlong3004.iom.api.domain.transaction.Transaction;
import me.nghlong3004.iom.api.domain.transaction.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@DisplayName("TransactionSummary Unit Tests")
class TransactionSummaryTest {

  @Test
  @DisplayName("Should return zero count for empty list")
  void from_EmptyList_ReturnsZeroCount() {
    var summary = TransactionSummary.from(List.of());

    assertThat(summary.transactionCount()).isZero();
    assertThat(summary.totals()).isEmpty();
  }

  @Test
  @DisplayName("Should group income and expense correctly for same currency")
  void from_MixedTypes_GroupsByCurrency() {
    var expense =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(30000L)
            .currency(Currency.VND)
            .build();
    var income =
        Transaction.builder()
            .type(TransactionType.INCOME)
            .amount(5000000L)
            .currency(Currency.VND)
            .build();

    var summary = TransactionSummary.from(List.of(expense, income));

    assertThat(summary.transactionCount()).isEqualTo(2);
    assertThat(summary.totals().get(Currency.VND).totalExpense()).isEqualTo(30000L);
    assertThat(summary.totals().get(Currency.VND).totalIncome()).isEqualTo(5000000L);
  }

  @Test
  @DisplayName("Should separate groups by currency")
  void from_MultipleCurrencies_SeparatesGroups() {
    var vndExpense =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(30000L)
            .currency(Currency.VND)
            .build();
    var usdExpense =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(1050L)
            .currency(Currency.USD)
            .build();

    var summary = TransactionSummary.from(List.of(vndExpense, usdExpense));

    assertThat(summary.transactionCount()).isEqualTo(2);
    assertThat(summary.totals()).containsOnlyKeys(Currency.VND, Currency.USD);
    assertThat(summary.totals().get(Currency.VND).totalExpense()).isEqualTo(30000L);
    assertThat(summary.totals().get(Currency.USD).totalExpense()).isEqualTo(1050L);
  }

  @Test
  @DisplayName("Should have zero expense when only income transactions exist")
  void from_IncomeOnly_HasZeroExpense() {
    var income =
        Transaction.builder()
            .type(TransactionType.INCOME)
            .amount(100000L)
            .currency(Currency.VND)
            .build();

    var summary = TransactionSummary.from(List.of(income));

    assertThat(summary.totals().get(Currency.VND).totalIncome()).isEqualTo(100000L);
    assertThat(summary.totals().get(Currency.VND).totalExpense()).isZero();
  }
}
