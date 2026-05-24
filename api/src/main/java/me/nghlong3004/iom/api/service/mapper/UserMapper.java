package me.nghlong3004.iom.api.service.mapper;

import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.user.AppUser;
import me.nghlong3004.iom.api.domain.user.ExternalAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  default AppUser toNewAppUser() {
    return AppUser.builder().build();
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", source = "user")
  @Mapping(target = "platform", source = "platform")
  @Mapping(target = "externalUserId", source = "externalUserId")
  @Mapping(target = "displayName", ignore = true)
  @Mapping(target = "linkedAt", ignore = true)
  ExternalAccount toExternalAccount(
      AppUser user, MessageChannel platform, String externalUserId);
}
