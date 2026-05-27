package me.nghlong3004.iom.api.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import me.nghlong3004.iom.api.application.port.out.UserResolver;
import me.nghlong3004.iom.api.common.BotMessages;
import me.nghlong3004.iom.api.common.SummaryFormatter;
import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import me.nghlong3004.iom.api.domain.message.MessageSender;
import me.nghlong3004.iom.api.domain.message.OutgoingMessage;
import me.nghlong3004.iom.api.domain.user.AppUser;
import me.nghlong3004.iom.api.service.TransactionService;
import me.nghlong3004.iom.api.service.TransactionSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MonthSummaryHandler Unit Tests")
class MonthSummaryHandlerTest {

  @Mock private UserResolver userResolver;
  @Mock private TransactionService transactionService;
  @Mock private MessageSender messageSender;
  @Mock private BotMessages botMessages;
  @Mock private SummaryFormatter summaryFormatter;

  @InjectMocks private MonthSummaryHandler handler;

  @Test
  @DisplayName("Should support /month command")
  void supports_MonthCommand_ReturnsTrue() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "/month");

    assertThat(handler.supports(message)).isTrue();
  }

  @Test
  @DisplayName("Should not support other commands")
  void supports_OtherCommand_ReturnsFalse() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "/today");

    assertThat(handler.supports(message)).isFalse();
  }

  @Test
  @DisplayName("Should resolve user, summarize, format and send reply")
  void handle_MonthCommand_SendsFormattedSummary() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "/month");
    var user = AppUser.builder().id(1L).build();
    var summary = new TransactionSummary(java.util.Map.of(), 0);
    given(userResolver.resolve(message)).willReturn(user);
    given(transactionService.summarize(any(), any(), any())).willReturn(summary);
    given(botMessages.monthLabel(anyInt(), anyInt())).willReturn("Thang 5/2026");
    given(summaryFormatter.format("Thang 5/2026", summary)).willReturn("month reply");

    handler.handle(message);

    var captor = ArgumentCaptor.forClass(OutgoingMessage.class);
    verify(messageSender).send(captor.capture());
    assertThat(captor.getValue().text()).isEqualTo("month reply");
  }

  @Test
  @DisplayName("Should always return true")
  void handle_MonthCommand_AlwaysReturnsTrue() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "/month");
    var user = AppUser.builder().id(1L).build();
    var summary = new TransactionSummary(java.util.Map.of(), 0);
    given(userResolver.resolve(message)).willReturn(user);
    given(transactionService.summarize(any(), any(), any())).willReturn(summary);
    given(botMessages.monthLabel(anyInt(), anyInt())).willReturn("Thang 5/2026");
    given(summaryFormatter.format("Thang 5/2026", summary)).willReturn("reply");

    var handled = handler.handle(message);

    assertThat(handled).isTrue();
  }
}
