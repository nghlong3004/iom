package me.nghlong3004.iom.api.channel.telegram;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import me.nghlong3004.iom.api.application.usecase.HandleIncomingMessageUseCase;
import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TelegramUpdateDispatcher Unit Tests")
class TelegramUpdateDispatcherTest {

  @Mock private TelegramMessageMapper mapper;
  @Mock private HandleIncomingMessageUseCase handleIncomingMessageUseCase;

  @InjectMocks private TelegramUpdateDispatcher dispatcher;

  @Test
  @DisplayName("Should dispatch valid text update to use case")
  void dispatch_ValidTextUpdate_DelegatesMessage() {
    var update = mock(Update.class);
    var message = mock(Message.class);
    given(update.hasMessage()).willReturn(true);
    given(update.getMessage()).willReturn(message);
    given(message.hasText()).willReturn(true);
    given(message.getChatId()).willReturn(12345L);
    var incomingMessage = mock(IncomingMessage.class);
    given(incomingMessage.externalUserId()).willReturn("u-1");
    given(incomingMessage.conversationId()).willReturn("chat-1");
    given(incomingMessage.normalizedText()).willReturn("hello");
    given(mapper.toIncomingMessage(update)).willReturn(incomingMessage);

    dispatcher.dispatch(update);

    verify(mapper).toIncomingMessage(update);
    verify(handleIncomingMessageUseCase).handle(incomingMessage);
  }

  @Test
  @DisplayName("Should skip update without message")
  void dispatch_NoMessage_Skips() {
    var update = mock(Update.class);
    given(update.hasMessage()).willReturn(false);

    dispatcher.dispatch(update);

    verify(mapper, never()).toIncomingMessage(update);
    verify(handleIncomingMessageUseCase, never()).handle(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("Should skip update without text")
  void dispatch_NoText_Skips() {
    var update = mock(Update.class);
    var message = mock(Message.class);
    given(update.hasMessage()).willReturn(true);
    given(update.getMessage()).willReturn(message);
    given(message.hasText()).willReturn(false);

    dispatcher.dispatch(update);

    verify(mapper, never()).toIncomingMessage(update);
  }

  @Test
  @DisplayName("Should skip update with null chat ID")
  void dispatch_NullChatId_Skips() {
    var update = mock(Update.class);
    var message = mock(Message.class);
    given(update.hasMessage()).willReturn(true);
    given(update.getMessage()).willReturn(message);
    given(message.hasText()).willReturn(true);
    given(message.getChatId()).willReturn(null);

    dispatcher.dispatch(update);

    verify(mapper, never()).toIncomingMessage(update);
  }
}
