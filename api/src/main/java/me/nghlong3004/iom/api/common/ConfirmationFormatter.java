package me.nghlong3004.iom.api.common;

import lombok.RequiredArgsConstructor;
import me.nghlong3004.iom.api.domain.transaction.ParsedTransaction;
import me.nghlong3004.iom.api.domain.transaction.TransactionType;
import org.springframework.stereotype.Component;

/**
 * Formats transaction confirmation messages for bot replies.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@Component
@RequiredArgsConstructor
public class ConfirmationFormatter {

  private final BotMessages botMessages;

  public String format(ParsedTransaction parsed) {
    var isIncome = parsed.type() == TransactionType.INCOME;
    var typeLabel = botMessages.typeLabel(isIncome);
    var formattedAmount = AmountFormatter.format(parsed.amount(), parsed.currency());

    return botMessages.transactionRecorded(typeLabel, formattedAmount, parsed.note());
  }
}
