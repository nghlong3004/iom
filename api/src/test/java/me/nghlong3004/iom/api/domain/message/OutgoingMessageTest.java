package me.nghlong3004.iom.api.domain.message;

import static org.assertj.core.api.Assertions.assertThat;

import me.nghlong3004.iom.api.domain.MessageChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@DisplayName("OutgoingMessage Unit Tests")
class OutgoingMessageTest {

  @Test
  @DisplayName("replyTo should copy channel and conversationId from incoming message")
  void replyTo_CopiesChannelAndConversationId() {
    var incoming = new IncomingMessage(MessageChannel.TELEGRAM, "u-1", "chat-42", "hello");

    var outgoing = OutgoingMessage.replyTo(incoming, "reply text");

    assertThat(outgoing.channel()).isEqualTo(MessageChannel.TELEGRAM);
    assertThat(outgoing.conversationId()).isEqualTo("chat-42");
    assertThat(outgoing.text()).isEqualTo("reply text");
  }
}
