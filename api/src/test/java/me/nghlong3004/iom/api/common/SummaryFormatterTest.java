package me.nghlong3004.iom.api.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Map;
import me.nghlong3004.iom.api.domain.summary.FlowFilter;
import me.nghlong3004.iom.api.domain.transaction.Currency;
import me.nghlong3004.iom.api.service.TransactionSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/26/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SummaryFormatter Unit Tests")
class SummaryFormatterTest {

  @Mock private BotMessages botMessages;

  private SummaryFormatter formatter;

  @BeforeEach
  void setUp() {
    formatter = new SummaryFormatter(botMessages);
  }

  @Test
  @DisplayName("Should format both expense and income by default")
  void format_AllFlowFilter_ReturnsBothSidesSummary() {
    var summary = summary();
    given(botMessages.summaryLine("50.000d", "100.000d", "VND"))
        .willReturn("  Chi 50.000d | Thu 100.000d (VND)");
    given(botMessages.summaryTotal(2)).willReturn("Tong: 2 giao dich.");

    var result = formatter.format("Hom qua", summary, FlowFilter.ALL);

    assertThat(result)
        .isEqualTo("Hom qua:\n  Chi 50.000d | Thu 100.000d (VND)\nTong: 2 giao dich.");
  }

  @Test
  @DisplayName("Should format expense-focused summary")
  void format_ExpenseFlowFilter_ReturnsExpenseSummary() {
    var summary = summary();
    given(botMessages.summaryExpenseLine("50.000d", "VND")).willReturn("  Chi 50.000d (VND)");
    given(botMessages.summaryTotal(2)).willReturn("Tong: 2 giao dich.");

    var result = formatter.format("Hom qua", summary, FlowFilter.EXPENSE);

    assertThat(result).isEqualTo("Hom qua:\n  Chi 50.000d (VND)\nTong: 2 giao dich.");
  }

  @Test
  @DisplayName("Should format income-focused summary")
  void format_IncomeFlowFilter_ReturnsIncomeSummary() {
    var summary = summary();
    given(botMessages.summaryIncomeLine("100.000d", "VND")).willReturn("  Thu 100.000d (VND)");
    given(botMessages.summaryTotal(2)).willReturn("Tong: 2 giao dich.");

    var result = formatter.format("Hom qua", summary, FlowFilter.INCOME);

    assertThat(result).isEqualTo("Hom qua:\n  Thu 100.000d (VND)\nTong: 2 giao dich.");
  }

  @Test
  @DisplayName("Should return empty message when no transactions")
  void format_EmptySummary_ReturnsEmptyMessage() {
    var summary = new TransactionSummary(java.util.Map.of(), 0);
    given(botMessages.summaryEmpty("Hom qua")).willReturn("Khong co giao dich.");

    var result = formatter.format("Hom qua", summary, FlowFilter.ALL);

    assertThat(result).isEqualTo("Khong co giao dich.");
  }

  @Test
  @DisplayName("Should default null flowFilter to ALL")
  void format_NullFlowFilter_DefaultsToAll() {
    var summary = summary();
    given(botMessages.summaryLine("50.000d", "100.000d", "VND"))
        .willReturn("  Chi 50.000d | Thu 100.000d (VND)");
    given(botMessages.summaryTotal(2)).willReturn("Tong: 2 giao dich.");

    var result = formatter.format("Hom qua", summary, null);

    assertThat(result)
        .isEqualTo("Hom qua:\n  Chi 50.000d | Thu 100.000d (VND)\nTong: 2 giao dich.");
  }

  @Test
  @DisplayName("Should use ALL filter when calling 2-arg format overload")
  void format_OverloadWithoutFilter_UsesAllDefault() {
    var summary = summary();
    given(botMessages.summaryLine("50.000d", "100.000d", "VND"))
        .willReturn("  Chi 50.000d | Thu 100.000d (VND)");
    given(botMessages.summaryTotal(2)).willReturn("Tong: 2 giao dich.");

    var result = formatter.format("Label", summary);

    assertThat(result)
        .isEqualTo("Label:\n  Chi 50.000d | Thu 100.000d (VND)\nTong: 2 giao dich.");
  }

  private TransactionSummary summary() {
    return new TransactionSummary(
        Map.of(Currency.VND, new TransactionSummary.CurrencyTotal(100000L, 50000L)), 2);
  }
}
