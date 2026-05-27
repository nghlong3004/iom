package me.nghlong3004.iom.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.user.AppUser;
import me.nghlong3004.iom.api.domain.user.Role;
import me.nghlong3004.iom.api.exception.ResourceException;
import me.nghlong3004.iom.api.repository.AppUserRepository;
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
@DisplayName("CustomUserDetailsService Unit Tests")
class CustomUserDetailsServiceTest {

  @Mock private AppUserRepository appUserRepository;

  @InjectMocks private CustomUserDetailsService service;

  @Test
  @DisplayName("Should return UserDetails for existing user with password")
  void loadByUsername_ExistingUser_ReturnsUserDetails() {
    var user =
        AppUser.builder()
            .email("test@example.com")
            .passwordHash("hashed")
            .isActive(true)
            .role(Role.USER)
            .build();
    given(appUserRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

    var result = service.loadUserByUsername("test@example.com");

    assertThat(result.getUsername()).isEqualTo("test@example.com");
    assertThat(result.getPassword()).isEqualTo("hashed");
    assertThat(result.isEnabled()).isTrue();
    assertThat(result.getAuthorities())
        .extracting("authority")
        .containsExactly("ROLE_USER");
  }

  @Test
  @DisplayName("Should return empty password when passwordHash is null")
  void loadByUsername_NullPassword_ReturnsUserDetailsWithEmptyPassword() {
    var user =
        AppUser.builder()
            .email("oauth@example.com")
            .passwordHash(null)
            .isActive(true)
            .role(Role.USER)
            .build();
    given(appUserRepository.findByEmail("oauth@example.com")).willReturn(Optional.of(user));

    var result = service.loadUserByUsername("oauth@example.com");

    assertThat(result.getPassword()).isEmpty();
  }

  @Test
  @DisplayName("Should throw ResourceException when email not found")
  void loadByUsername_EmailNotFound_ThrowsResourceException() {
    given(appUserRepository.findByEmail("unknown@example.com")).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadUserByUsername("unknown@example.com"))
        .isInstanceOf(ResourceException.class);
  }

  @Test
  @DisplayName("Should assign ROLE_ADMIN authority for admin user")
  void loadByUsername_AdminRole_HasRoleAdmin() {
    var user =
        AppUser.builder()
            .email("admin@example.com")
            .passwordHash("hashed")
            .isActive(true)
            .role(Role.ADMIN)
            .build();
    given(appUserRepository.findByEmail("admin@example.com")).willReturn(Optional.of(user));

    var result = service.loadUserByUsername("admin@example.com");

    assertThat(result.getAuthorities())
        .extracting("authority")
        .containsExactly("ROLE_ADMIN");
  }
}
