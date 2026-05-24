package me.nghlong3004.iom.api.application.command;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import me.nghlong3004.iom.api.application.port.out.UserResolver;
import me.nghlong3004.iom.api.common.AmountFormatter;
import me.nghlong3004.iom.api.common.BotMessages;
import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import me.nghlong3004.iom.api.domain.message.MessageSender;
import me.nghlong3004.iom.api.domain.message.OutgoingMessage;
import me.nghlong3004.iom.api.service.TransactionService;
import me.nghlong3004.iom.api.service.TransactionSummary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Handles the {@code /today} command. Returns a summary of today's transactions grouped by
 * currency.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@Component
@Order(3)
@RequiredArgsConstructor
public class TodaySummaryHandler implements BotCommandHandler {

  private final UserResolver userResolver;
  private final TransactionService transactionService;
  private final MessageSender messageSender;
  private final BotMessages botMessages;

  @Override
  public boolean supports(IncomingMessage message) {
    return message.normalizedText().equalsIgnoreCase(BotCommand.TODAY.getCommand());
  }

  @Override
  public void handle(IncomingMessage message) {
    var user = userResolver.resolve(message);
    var zone = ZoneId.systemDefault();
    var today = LocalDate.now(zone);
    var from = today.atStartOfDay(zone).toInstant();
    var to = today.plusDays(1).atStartOfDay(zone).toInstant();

    var summary = transactionService.summarize(user, from, to);
    var reply = formatSummary(botMessages.todayLabel(), summary);
    messageSender.send(OutgoingMessage.replyTo(message, reply));
  }

  String formatSummary(String label, TransactionSummary summary) {
    if (summary.transactionCount() == 0) {
      return botMessages.summaryEmpty(label);
    }

    var sb = new StringBuilder(label).append(":\n");
    summary
        .totals()
        .forEach(
            (currency, total) -> {
              var income = AmountFormatter.format(total.totalIncome(), currency);
              var expense = AmountFormatter.format(total.totalExpense(), currency);
              sb.append(botMessages.summaryLine(expense, income, currency.name())).append("\n");
            });
    sb.append(botMessages.summaryTotal(summary.transactionCount()));
    return sb.toString();
  }
}
