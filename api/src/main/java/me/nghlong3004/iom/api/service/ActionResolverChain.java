package me.nghlong3004.iom.api.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.nghlong3004.iom.api.application.port.out.ActionResolver;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;
import org.springframework.stereotype.Component;

/**
 * Orchestrates a chain of {@link ActionResolver} implementations. Tries each resolver in
 * {@link org.springframework.core.annotation.Order} sequence and returns the first successful
 * result.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@Component
@RequiredArgsConstructor
public class ActionResolverChain {

  private final List<ActionResolver> resolvers;

  /**
   * Resolves a transaction action by trying each resolver in order.
   *
   * @param normalizedText the user's message text
   * @return the first successfully resolved action, or empty if no resolver matched
   */
  public Optional<TransactionAction> resolve(String normalizedText) {
    return resolvers.stream()
        .map(resolver -> resolver.resolve(normalizedText))
        .flatMap(Optional::stream)
        .findFirst();
  }
}
