package me.nghlong3004.iom.api.domain.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TransactionAction Unit Tests")
class TransactionActionTest {

  @Test
  @DisplayName("Delete should reject null reference")
  void delete_NullReference_ThrowsNpe() {
    assertThatThrownBy(() -> new TransactionAction.Delete(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("reference is required");
  }

  @Test
  @DisplayName("Delete should accept valid reference")
  void delete_ValidReference_CreatesInstance() {
    var action = new TransactionAction.Delete(new TransactionReference.Latest());
    assertThat(action.reference()).isInstanceOf(TransactionReference.Latest.class);
  }

  @Test
  @DisplayName("Update should reject null reference")
  void update_NullReference_ThrowsNpe() {
    var changes = new UpdateFields(50000L, null, null, null);
    assertThatThrownBy(() -> new TransactionAction.Update(null, changes))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("reference is required");
  }

  @Test
  @DisplayName("Update should reject null changes")
  void update_NullChanges_ThrowsNpe() {
    var ref = new TransactionReference.Latest();
    assertThatThrownBy(() -> new TransactionAction.Update(ref, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("changes is required");
  }

  @Test
  @DisplayName("Update should accept valid reference and changes")
  void update_Valid_CreatesInstance() {
    var ref = new TransactionReference.ByIndex(2);
    var changes = new UpdateFields(50000L, null, null, null);
    var action = new TransactionAction.Update(ref, changes);
    assertThat(action.reference()).isEqualTo(ref);
    assertThat(action.changes()).isEqualTo(changes);
  }

  @Test
  @DisplayName("Undo, Confirm, Cancel should create instances")
  void metaActions_CreateInstances() {
    assertThat(new TransactionAction.Undo()).isInstanceOf(TransactionAction.class);
    assertThat(new TransactionAction.Confirm()).isInstanceOf(TransactionAction.class);
    assertThat(new TransactionAction.Cancel()).isInstanceOf(TransactionAction.class);
  }
}
