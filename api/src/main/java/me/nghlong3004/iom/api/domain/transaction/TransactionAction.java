package me.nghlong3004.iom.api.domain.transaction;

import java.util.Objects;

/**
 * Sealed hierarchy representing user actions on existing transactions.
 *
 * <p>Use exhaustive {@code switch} expressions for compiler-verified handling:
 *
 * <pre>{@code
 * switch (action) {
 *     case TransactionAction.Delete d  -> handleDelete(d);
 *     case TransactionAction.Update u  -> handleUpdate(u);
 *     case TransactionAction.Undo u    -> handleUndo();
 *     case TransactionAction.Confirm c -> handleConfirm();
 *     case TransactionAction.Cancel c  -> handleCancel();
 * }
 * }</pre>
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
public sealed interface TransactionAction {

  /** Request to delete a transaction. */
  record Delete(TransactionReference reference) implements TransactionAction {
    public Delete {
      Objects.requireNonNull(reference, "reference is required");
    }
  }

  /** Request to update a transaction with partial changes. */
  record Update(TransactionReference reference, UpdateFields changes)
      implements TransactionAction {
    public Update {
      Objects.requireNonNull(reference, "reference is required");
      Objects.requireNonNull(changes, "changes is required");
    }
  }

  /** Undo the last recorded transaction (delete it). */
  record Undo() implements TransactionAction {}

  /** Confirm a pending action (e.g., delete confirmation). */
  record Confirm() implements TransactionAction {}

  /** Cancel a pending action. */
  record Cancel() implements TransactionAction {}
}
