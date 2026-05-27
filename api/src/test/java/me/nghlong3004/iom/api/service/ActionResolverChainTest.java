package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import me.nghlong3004.iom.api.application.port.out.ActionResolver;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;
import me.nghlong3004.iom.api.domain.transaction.TransactionReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ActionResolverChain Unit Tests")
@ExtendWith(MockitoExtension.class)
class ActionResolverChainTest {

  @Mock private ActionResolver first;
  @Mock private ActionResolver second;

  @Test
  @DisplayName("Should return first resolver result when it matches")
  void resolve_FirstMatches_ReturnsFirst() {
    var action = new TransactionAction.Undo();
    given(first.resolve("undo")).willReturn(Optional.of(action));
    var chain = new ActionResolverChain(List.of(first, second));

    var result = chain.resolve("undo");

    assertThat(result).contains(action);
  }

  @Test
  @DisplayName("Should fallback to second resolver when first returns empty")
  void resolve_FirstEmpty_FallsBackToSecond() {
    var action = new TransactionAction.Delete(new TransactionReference.ByMatch("ăn sáng"));
    given(first.resolve("xoa cai an sang")).willReturn(Optional.empty());
    given(second.resolve("xoa cai an sang")).willReturn(Optional.of(action));
    var chain = new ActionResolverChain(List.of(first, second));

    var result = chain.resolve("xoa cai an sang");

    assertThat(result).contains(action);
  }

  @Test
  @DisplayName("Should return empty when all resolvers return empty")
  void resolve_AllEmpty_ReturnsEmpty() {
    given(first.resolve("hello")).willReturn(Optional.empty());
    given(second.resolve("hello")).willReturn(Optional.empty());
    var chain = new ActionResolverChain(List.of(first, second));

    var result = chain.resolve("hello");

    assertThat(result).isEmpty();
  }
}
