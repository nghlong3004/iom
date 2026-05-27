package me.nghlong3004.iom.api.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import me.nghlong3004.iom.api.application.port.out.ActionResolver;
import me.nghlong3004.iom.api.config.BotIntentProperties;
import me.nghlong3004.iom.api.domain.transaction.TransactionAction;
import me.nghlong3004.iom.api.domain.transaction.TransactionReference;
import me.nghlong3004.iom.api.domain.transaction.UpdateFields;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Deterministic {@link ActionResolver} that resolves transaction management actions using keyword
 * matching. Handles ~90% of cases without LLM calls.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class KeywordActionResolver implements ActionResolver {

  private static final Pattern AMOUNT_PATTERN =
      Pattern.compile("(\\d+)\\s*k\\b", Pattern.CASE_INSENSITIVE);

  private final BotIntentProperties botIntentProperties;

  @Override
  public Optional<TransactionAction> resolve(String normalizedText) {
    if (normalizedText == null || normalizedText.isBlank()) {
      return Optional.empty();
    }

    var text = stripAccents(normalizedText);
    var manage = botIntentProperties.manageAction();
    if (manage == null) {
      return Optional.empty();
    }

    // Confirm / Cancel — highest priority (responds to pending state)
    if (hasAny(text, manage.confirmKeywords())) {
      return Optional.of(new TransactionAction.Confirm());
    }
    if (hasAny(text, manage.cancelKeywords())) {
      return Optional.of(new TransactionAction.Cancel());
    }

    // Undo
    if (hasAny(text, manage.undoKeywords())) {
      return Optional.of(new TransactionAction.Undo());
    }

    // Delete
    if (hasAny(text, manage.deleteKeywords())) {
      var ref = resolveReference(text, manage);
      if (ref.isPresent()) {
        return Optional.of(new TransactionAction.Delete(ref.get()));
      }
    }

    // Update
    if (hasAny(text, manage.updateKeywords())) {
      var ref = resolveReference(text, manage);
      if (ref.isPresent()) {
        var changes = parseUpdateChanges(text);
        return Optional.of(new TransactionAction.Update(ref.get(), changes));
      }
    }

    return Optional.empty();
  }

  private Optional<TransactionReference> resolveReference(
      String text, BotIntentProperties.ManageAction manage) {
    // Check for index pattern: "số 2", "so 3"
    if (manage.indexPattern() != null) {
      var indexMatcher = Pattern.compile(manage.indexPattern()).matcher(text);
      if (indexMatcher.find()) {
        var index = Integer.parseInt(indexMatcher.group(1));
        return Optional.of(new TransactionReference.ByIndex(index));
      }
    }

    // Check for "latest" keywords
    if (hasAny(text, manage.latestKeywords())) {
      return Optional.of(new TransactionReference.Latest());
    }

    return Optional.empty();
  }

  private UpdateFields parseUpdateChanges(String text) {
    // Simple amount extraction: "thành 50k" → 50000
    Long amount = null;
    var amountMatcher = AMOUNT_PATTERN.matcher(text);
    if (amountMatcher.find()) {
      amount = Long.parseLong(amountMatcher.group(1)) * 1000;
    }
    return new UpdateFields(amount, null, null, null);
  }

  private boolean hasAny(String text, Iterable<String> keywords) {
    if (keywords == null) {
      return false;
    }
    for (String keyword : keywords) {
      if (text.contains(stripAccents(keyword))) {
        return true;
      }
    }
    return false;
  }

  private String stripAccents(String text) {
    var decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
    return decomposed
        .replaceAll("\\p{M}", "")
        .replace('\u0111', 'd')
        .replace('\u0110', 'D')
        .toLowerCase(Locale.ROOT)
        .trim();
  }
}
