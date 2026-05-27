package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.MessageChannel;
import me.nghlong3004.iom.api.domain.message.IncomingMessage;
import me.nghlong3004.iom.api.domain.user.AppUser;
import me.nghlong3004.iom.api.domain.user.ExternalAccount;
import me.nghlong3004.iom.api.repository.AppUserRepository;
import me.nghlong3004.iom.api.repository.ExternalAccountRepository;
import me.nghlong3004.iom.api.service.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/27/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultUserResolver Unit Tests")
class DefaultUserResolverTest {

  @Mock private ExternalAccountRepository externalAccountRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private UserMapper userMapper;

  @InjectMocks private DefaultUserResolver resolver;

  @Test
  @DisplayName("Should return existing user when external account found")
  void resolve_ExistingExternalAccount_ReturnsExistingUser() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "tg-123", "chat-1", "hello");
    var existingUser = AppUser.builder().id(1L).build();
    var externalAccount = ExternalAccount.builder().user(existingUser).build();
    given(externalAccountRepository.findByPlatformAndExternalUserId(MessageChannel.TELEGRAM, "tg-123"))
        .willReturn(Optional.of(externalAccount));

    var result = resolver.resolve(message);

    assertThat(result).isSameAs(existingUser);
  }

  @Test
  @DisplayName("Should create and return new user when no external account found")
  void resolve_NewExternalAccount_CreatesAndReturnsUser() {
    var message = new IncomingMessage(MessageChannel.TELEGRAM, "tg-999", "chat-1", "hello");
    var newUser = AppUser.builder().build();
    var savedUser = AppUser.builder().id(2L).build();
    var externalAccount = ExternalAccount.builder().build();
    given(externalAccountRepository.findByPlatformAndExternalUserId(MessageChannel.TELEGRAM, "tg-999"))
        .willReturn(Optional.empty());
    given(userMapper.toNewAppUser()).willReturn(newUser);
    given(appUserRepository.save(newUser)).willReturn(savedUser);
    given(userMapper.toExternalAccount(savedUser, MessageChannel.TELEGRAM, "tg-999"))
        .willReturn(externalAccount);

    var result = resolver.resolve(message);

    assertThat(result).isSameAs(savedUser);
    verify(appUserRepository).save(newUser);
    verify(externalAccountRepository).save(externalAccount);
  }

  @Test
  @DisplayName("Should throw NullPointerException when message is null")
  void resolve_NullMessage_ThrowsNpe() {
    assertThatThrownBy(() -> resolver.resolve(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("message must not be null");
  }
}
