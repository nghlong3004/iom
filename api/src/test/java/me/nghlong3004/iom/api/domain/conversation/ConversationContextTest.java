package me.nghlong3004.iom.api.domain.conversation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;
import me.nghlong3004.iom.api.domain.transaction.TransactionReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ConversationContext Unit Tests")
class ConversationContextTest {

  private ConversationContext context;

  @BeforeEach
  void setUp() {
    context = new ConversationContext("TELEGRAM:user-1");
  }

  @Test
  @DisplayName("Should start in IDLE state")
  void newContext_IsIdle() {
    assertThat(context.getState()).isEqualTo(ConversationContext.ConversationState.IDLE);
    assertThat(context.isAwaitingConfirmation()).isFalse();
  }

  @Test
  @DisplayName("Should track last recorded transaction ID")
  void setLastRecordedTransactionId_SetsValue() {
    context.setLastRecordedTransactionId(42L);
    assertThat(context.getLastRecordedTransactionId()).isEqualTo(42L);
    assertThat(context.hasLastRecorded()).isTrue();
  }

  @Test
  @DisplayName("Should track last viewed transaction IDs")
  void setLastViewedTransactionIds_SetsValue() {
    context.setLastViewedTransactionIds(List.of(1L, 2L, 3L));
    assertThat(context.getLastViewedTransactionIds()).containsExactly(1L, 2L, 3L);
    assertThat(context.hasViewedList()).isTrue();
  }

  @Test
  @DisplayName("Should resolve by 1-based index")
  void resolveByIndex_ValidIndex_ReturnsId() {
    context.setLastViewedTransactionIds(List.of(10L, 20L, 30L));
    assertThat(context.resolveByIndex(1)).isEqualTo(10L);
    assertThat(context.resolveByIndex(2)).isEqualTo(20L);
    assertThat(context.resolveByIndex(3)).isEqualTo(30L);
  }

  @Test
  @DisplayName("Should return null for out-of-range index")
  void resolveByIndex_OutOfRange_ReturnsNull() {
    context.setLastViewedTransactionIds(List.of(10L, 20L));
    assertThat(context.resolveByIndex(0)).isNull();
    assertThat(context.resolveByIndex(3)).isNull();
  }

  @Test
  @DisplayName("Should transition to AWAITING_CONFIRMATION on setPending")
  void setPending_TransitionsToAwaitingConfirmation() {
    var action = new TransactionAction.Delete(new TransactionReference.Latest());
    context.setPending(action, null);

    assertThat(context.isAwaitingConfirmation()).isTrue();
    assertThat(context.getPendingAction()).isEqualTo(action);
  }

  @Test
  @DisplayName("Should return to IDLE on clearPending")
  void clearPending_ReturnsToIdle() {
    var action = new TransactionAction.Delete(new TransactionReference.Latest());
    context.setPending(action, null);
    context.clearPending();

    assertThat(context.isAwaitingConfirmation()).isFalse();
    assertThat(context.getPendingAction()).isNull();
    assertThat(context.getPendingTarget()).isNull();
  }

  @Test
  @DisplayName("Should handle null viewed list gracefully")
  void setLastViewedTransactionIds_Null_SetsEmpty() {
    context.setLastViewedTransactionIds(null);
    assertThat(context.hasViewedList()).isFalse();
  }

  @Test
  @DisplayName("Should update lastActivityAt on mutations")
  void mutations_UpdateLastActivityAt() {
    var before = context.getLastActivityAt();
    context.setLastRecordedTransactionId(1L);
    assertThat(context.getLastActivityAt()).isAfterOrEqualTo(before);
  }
}
