package me.nghlong3004.iom.api.domain.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TransactionReference Unit Tests")
class TransactionReferenceTest {

  @Test
  @DisplayName("Latest should create instance")
  void latest_CreatesInstance() {
    var ref = new TransactionReference.Latest();
    assertThat(ref).isInstanceOf(TransactionReference.class);
  }

  @Test
  @DisplayName("ByIndex should accept valid index")
  void byIndex_ValidIndex_CreatesInstance() {
    var ref = new TransactionReference.ByIndex(1);
    assertThat(ref.index()).isEqualTo(1);
  }

  @Test
  @DisplayName("ByIndex should reject zero index")
  void byIndex_ZeroIndex_ThrowsException() {
    assertThatThrownBy(() -> new TransactionReference.ByIndex(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("index must be >= 1");
  }

  @Test
  @DisplayName("ByIndex should reject negative index")
  void byIndex_NegativeIndex_ThrowsException() {
    assertThatThrownBy(() -> new TransactionReference.ByIndex(-1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("ByMatch should accept valid description")
  void byMatch_ValidDescription_CreatesInstance() {
    var ref = new TransactionReference.ByMatch("ăn sáng 30k");
    assertThat(ref.description()).isEqualTo("ăn sáng 30k");
  }

  @Test
  @DisplayName("ByMatch should reject null description")
  void byMatch_NullDescription_ThrowsNpe() {
    assertThatThrownBy(() -> new TransactionReference.ByMatch(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("ByMatch should reject blank description")
  void byMatch_BlankDescription_ThrowsException() {
    assertThatThrownBy(() -> new TransactionReference.ByMatch("  "))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
