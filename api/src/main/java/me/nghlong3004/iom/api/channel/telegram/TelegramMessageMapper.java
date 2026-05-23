package me.nghlong3004.iom.api.channel.telegram;

import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/23/2026
 */
@Mapper(componentModel = "spring")
public interface TelegramMessageMapper {

  @Mapping(target = "channel", constant = "TELEGRAM")
  @Mapping(target = "externalUserId", expression = "java(extractExternalUserId(update))")
  @Mapping(target = "conversationId", expression = "java(String.valueOf(update.getMessage().getChatId()))")
  @Mapping(target = "text", expression = "java(update.getMessage().getText())")
  IncomingMessage toIncomingMessage(Update update);

  default String extractExternalUserId(Update update) {
    var from = update.getMessage().getFrom();
    return from == null ? null : String.valueOf(from.getId());
  }
}
