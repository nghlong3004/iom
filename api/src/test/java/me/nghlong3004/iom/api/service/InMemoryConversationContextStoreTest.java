package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import me.nghlong3004.iom.api.domain.conversation.ConversationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InMemoryConversationContextStore Unit Tests")
class InMemoryConversationContextStoreTest {

  private final InMemoryConversationContextStore store = new InMemoryConversationContextStore();

  @Test
  @DisplayName("Should create new context for unknown key")
  void get_NewKey_CreatesContext() {
    var context = store.get("TELEGRAM:user-1");
    assertThat(context).isNotNull();
    assertThat(context.getConversationKey()).isEqualTo("TELEGRAM:user-1");
    assertThat(context.getState())
        .isEqualTo(ConversationContext.ConversationState.IDLE);
  }

  @Test
  @DisplayName("Should return same context for same key")
  void get_SameKey_ReturnsSameContext() {
    var ctx1 = store.get("TELEGRAM:user-1");
    ctx1.setLastRecordedTransactionId(42L);
    var ctx2 = store.get("TELEGRAM:user-1");
    assertThat(ctx2.getLastRecordedTransactionId()).isEqualTo(42L);
  }

  @Test
  @DisplayName("Should save and retrieve updated context")
  void save_UpdatesContext() {
    var context = store.get("TELEGRAM:user-2");
    context.setLastRecordedTransactionId(99L);
    store.save(context);

    var retrieved = store.get("TELEGRAM:user-2");
    assertThat(retrieved.getLastRecordedTransactionId()).isEqualTo(99L);
  }

  @Test
  @DisplayName("Different keys should have different contexts")
  void get_DifferentKeys_ReturnDifferentContexts() {
    var ctx1 = store.get("TELEGRAM:user-1");
    var ctx2 = store.get("TELEGRAM:user-2");
    ctx1.setLastRecordedTransactionId(1L);
    assertThat(ctx2.getLastRecordedTransactionId()).isNull();
  }
}
