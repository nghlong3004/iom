package me.nghlong3004.iom.api.channel.telegram;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import me.nghlong3004.iom.api.config.TelegramBotProperties;
import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.message.OutgoingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TelegramReplySender Unit Tests")
class TelegramReplySenderTest {

  @Mock private TelegramClient telegramClient;

  private TelegramReplySender sender;

  @BeforeEach
  void setUp() throws Exception {
    sender = new TelegramReplySender(new TelegramBotProperties(true, "dummy-token", "dummy-bot"));
    Field clientField = TelegramReplySender.class.getDeclaredField("telegramClient");
    clientField.setAccessible(true);
    clientField.set(sender, telegramClient);
  }

  @Test
  @DisplayName("Should execute SendMessage for Telegram outgoing message")
  void send_TelegramMessage_ExecutesSendMessage() throws TelegramApiException {
    var message = new OutgoingMessage(MessageChannel.TELEGRAM, "chat-42", "hello user");

    sender.send(message);

    verify(telegramClient).execute(any(SendMessage.class));
  }

  @Test
  @DisplayName("Should skip non-Telegram messages")
  void send_NonTelegramMessage_Skips() throws TelegramApiException {
    var message = new OutgoingMessage(null, "chat-42", "hello");

    sender.send(message);

    verify(telegramClient, never()).execute(any(SendMessage.class));
  }

  @Test
  @DisplayName("Should not propagate TelegramApiException")
  void send_TelegramApiException_LogsError() throws TelegramApiException {
    var message = new OutgoingMessage(MessageChannel.TELEGRAM, "chat-42", "hello");
    willThrow(new TelegramApiException("network error"))
        .given(telegramClient)
        .execute(any(SendMessage.class));

    // Should not throw — exception is caught and logged
    sender.send(message);

    verify(telegramClient).execute(any(SendMessage.class));
  }
}
