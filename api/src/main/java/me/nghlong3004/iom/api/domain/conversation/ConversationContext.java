package me.nghlong3004.iom.api.domain.conversation;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import me.nghlong3004.iom.api.domain.transaction.Transaction;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;

/**
 * Per-user conversation context for stateful interactions. Tracks the last recorded transaction,
 * last viewed transaction list, and any pending action awaiting confirmation.
 *
 * <p>Platform-agnostic — works identically for Telegram, Web, Zalo, etc.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@Getter
public class ConversationContext {

  private final String conversationKey;
  private Long lastRecordedTransactionId;
  private List<Long> lastViewedTransactionIds;
  private TransactionAction pendingAction;
  private Transaction pendingTarget;
  private ConversationState state;
  private Instant lastActivityAt;

  public ConversationContext(String conversationKey) {
    this.conversationKey = conversationKey;
    this.lastViewedTransactionIds = Collections.emptyList();
    this.state = ConversationState.IDLE;
    this.lastActivityAt = Instant.now();
  }

  public enum ConversationState {
    IDLE,
    AWAITING_CONFIRMATION
  }

  public void setLastRecordedTransactionId(Long transactionId) {
    this.lastRecordedTransactionId = transactionId;
    touch();
  }

  public void setLastViewedTransactionIds(List<Long> transactionIds) {
    this.lastViewedTransactionIds =
        transactionIds != null ? Collections.unmodifiableList(transactionIds) : Collections.emptyList();
    touch();
  }

  /**
   * Sets a pending action that requires user confirmation.
   *
   * @param action the action awaiting confirmation
   * @param target the resolved transaction that will be affected
   */
  public void setPending(TransactionAction action, Transaction target) {
    this.pendingAction = action;
    this.pendingTarget = target;
    this.state = ConversationState.AWAITING_CONFIRMATION;
    touch();
  }

  /** Clears any pending action and returns to IDLE state. */
  public void clearPending() {
    this.pendingAction = null;
    this.pendingTarget = null;
    this.state = ConversationState.IDLE;
    touch();
  }

  public boolean isAwaitingConfirmation() {
    return state == ConversationState.AWAITING_CONFIRMATION;
  }

  /**
   * Resolves a transaction ID from the last viewed list by 1-based index.
   *
   * @param index 1-based index
   * @return the transaction ID, or null if out of range
   */
  public Long resolveByIndex(int index) {
    if (lastViewedTransactionIds == null || index < 1 || index > lastViewedTransactionIds.size()) {
      return null;
    }
    return lastViewedTransactionIds.get(index - 1);
  }

  public boolean hasLastRecorded() {
    return lastRecordedTransactionId != null;
  }

  public boolean hasViewedList() {
    return lastViewedTransactionIds != null && !lastViewedTransactionIds.isEmpty();
  }

  private void touch() {
    this.lastActivityAt = Instant.now();
  }
}
