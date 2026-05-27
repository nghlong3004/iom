package me.nghlong3004.iom.api.domain.message;

import static org.assertj.core.api.Assertions.assertThat;

import me.nghlong3004.iom.api.domain.MessageChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@DisplayName("IncomingMessage Unit Tests")
class IncomingMessageTest {

  @Test
  @DisplayName("normalizedText should trim whitespace")
  void normalizedText_TrimsWhitespace() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "  hello  ");

    assertThat(message.normalizedText()).isEqualTo("hello");
  }

  @Test
  @DisplayName("normalizedText should return empty when text is null")
  void normalizedText_NullText_ReturnsEmpty() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", null);

    assertThat(message.normalizedText()).isEmpty();
  }

  @Test
  @DisplayName("hasText should return true when message has content")
  void hasText_WithContent_ReturnsTrue() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "hello");

    assertThat(message.hasText()).isTrue();
  }

  @Test
  @DisplayName("hasText should return false when text is blank")
  void hasText_BlankText_ReturnsFalse() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "   ");

    assertThat(message.hasText()).isFalse();
  }

  @Test
  @DisplayName("isCommand should return true for slash-prefixed text")
  void isCommand_SlashPrefix_ReturnsTrue() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "/start");

    assertThat(message.isCommand()).isTrue();
  }

  @Test
  @DisplayName("isCommand should return false for plain text")
  void isCommand_PlainText_ReturnsFalse() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-1", "hello");

    assertThat(message.isCommand()).isFalse();
  }
}
