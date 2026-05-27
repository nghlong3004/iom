package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.nghlong3004.iom.api.config.BotIntentProperties;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;
import me.nghlong3004.iom.api.domain.transaction.TransactionReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("KeywordActionResolver Unit Tests")
class KeywordActionResolverTest {

  private final BotIntentProperties properties =
      new BotIntentProperties(
          new BotIntentProperties.Summary(
              List.of("xem"),
              List.of("hom nay"),
              List.of("hom qua"),
              List.of("hom kia"),
              List.of("tuan nay"),
              List.of("thang nay"),
              List.of("chi"),
              List.of("thu"),
              List.of("mua gi")),
          new BotIntentProperties.ManageAction(
              List.of("xoa", "xóa"),
              List.of("sua", "sửa"),
              List.of("undo", "hoan tac"),
              List.of("ok", "co", "có", "dong y"),
              List.of("huy", "hủy", "khong", "thoi"),
              List.of("vua roi", "vừa rồi", "cuoi", "cuối"),
              "(?:so|số)\\s*(\\d+)"));

  private final KeywordActionResolver resolver = new KeywordActionResolver(properties);

  @Test
  @DisplayName("Should resolve 'xoa cai vua roi' to Delete(Latest)")
  void resolve_DeleteLatest() {
    var result = resolver.resolve("xoa cai vua roi");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Delete.class);
    var delete = (TransactionAction.Delete) result.get();
    assertThat(delete.reference()).isInstanceOf(TransactionReference.Latest.class);
  }

  @Test
  @DisplayName("Should resolve 'xóa số 2' to Delete(ByIndex(2))")
  void resolve_DeleteByIndex() {
    var result = resolver.resolve("xóa số 2");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Delete.class);
    var delete = (TransactionAction.Delete) result.get();
    assertThat(delete.reference()).isInstanceOf(TransactionReference.ByIndex.class);
    assertThat(((TransactionReference.ByIndex) delete.reference()).index()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should resolve 'sua so 1 thanh 50k' to Update(ByIndex(1))")
  void resolve_UpdateByIndex() {
    var result = resolver.resolve("sua so 1 thanh 50k");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Update.class);
    var update = (TransactionAction.Update) result.get();
    assertThat(update.reference()).isInstanceOf(TransactionReference.ByIndex.class);
    assertThat(update.changes().amount()).isEqualTo(50000L);
  }

  @Test
  @DisplayName("Should resolve 'sua cai vua roi thanh 60k' to Update(Latest)")
  void resolve_UpdateLatest() {
    var result = resolver.resolve("sua cai vua roi thanh 60k");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Update.class);
    var update = (TransactionAction.Update) result.get();
    assertThat(update.reference()).isInstanceOf(TransactionReference.Latest.class);
    assertThat(update.changes().amount()).isEqualTo(60000L);
  }

  @Test
  @DisplayName("Should resolve 'undo' to Undo")
  void resolve_Undo() {
    var result = resolver.resolve("undo");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Undo.class);
  }

  @Test
  @DisplayName("Should resolve 'ok' to Confirm")
  void resolve_Confirm() {
    var result = resolver.resolve("ok");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Confirm.class);
  }

  @Test
  @DisplayName("Should resolve 'huy' to Cancel")
  void resolve_Cancel() {
    var result = resolver.resolve("huy");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Cancel.class);
  }

  @Test
  @DisplayName("Should resolve 'hoan tac' to Undo")
  void resolve_HoanTac_Undo() {
    var result = resolver.resolve("hoan tac");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Undo.class);
  }

  @Test
  @DisplayName("Should return empty for unrelated text")
  void resolve_Unrelated_ReturnsEmpty() {
    var result = resolver.resolve("hello world");
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return empty for null text")
  void resolve_Null_ReturnsEmpty() {
    var result = resolver.resolve(null);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle Vietnamese accented text")
  void resolve_AccentedText_Resolves() {
    var result = resolver.resolve("xóa cái vừa rồi");
    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(TransactionAction.Delete.class);
  }
}
