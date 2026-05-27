package me.nghlong3004.iom.api.domain.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@DisplayName("ParsedSummaryIntent Unit Tests")
class ParsedSummaryIntentTest {

  private static final Instant FROM = Instant.parse("2026-05-25T00:00:00Z");
  private static final Instant TO = Instant.parse("2026-05-26T00:00:00Z");

  @Test
  @DisplayName("summary factory should create valid intent")
  void summary_ValidInput_CreatesIntent() {
    var intent = ParsedSummaryIntent.summary(FROM, TO, "Hom qua", FlowFilter.EXPENSE);

    assertThat(intent.from()).isEqualTo(FROM);
    assertThat(intent.to()).isEqualTo(TO);
    assertThat(intent.label()).isEqualTo("Hom qua");
    assertThat(intent.flowFilter()).isEqualTo(FlowFilter.EXPENSE);
    assertThat(intent.needsClarification()).isFalse();
  }

  @Test
  @DisplayName("summary should reject null from")
  void summary_NullFrom_ThrowsNpe() {
    assertThatThrownBy(() -> ParsedSummaryIntent.summary(null, TO, "Label", FlowFilter.ALL))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("from is required");
  }

  @Test
  @DisplayName("summary should reject null to")
  void summary_NullTo_ThrowsNpe() {
    assertThatThrownBy(() -> ParsedSummaryIntent.summary(FROM, null, "Label", FlowFilter.ALL))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("to is required");
  }

  @Test
  @DisplayName("summary should reject from after to")
  void summary_FromAfterTo_ThrowsIllegalArgument() {
    assertThatThrownBy(() -> ParsedSummaryIntent.summary(TO, FROM, "Label", FlowFilter.ALL))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("from must be before to");
  }

  @Test
  @DisplayName("summary should reject blank label")
  void summary_BlankLabel_ThrowsIllegalArgument() {
    assertThatThrownBy(() -> ParsedSummaryIntent.summary(FROM, TO, "  ", FlowFilter.ALL))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label is required");
  }

  @Test
  @DisplayName("summary should default null flowFilter to ALL")
  void summary_NullFlowFilter_DefaultsToAll() {
    var intent = ParsedSummaryIntent.summary(FROM, TO, "Label", null);

    assertThat(intent.flowFilter()).isEqualTo(FlowFilter.ALL);
  }

  @Test
  @DisplayName("clarification factory should create clarification intent")
  void clarification_ValidMessage_CreatesClarification() {
    var intent = ParsedSummaryIntent.clarification("Ban muon xem ngay nao?");

    assertThat(intent.needsClarification()).isTrue();
    assertThat(intent.clarificationMessage()).isEqualTo("Ban muon xem ngay nao?");
  }

  @Test
  @DisplayName("clarification should reject blank message")
  void clarification_BlankMessage_ThrowsIllegalArgument() {
    assertThatThrownBy(() -> ParsedSummaryIntent.clarification("  "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("clarificationMessage is required");
  }
}
