package me.nghlong3004.iom.api.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nghlong3004.iom.api.domain.MessageChannel;
import org.hibernate.annotations.CreationTimestamp;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@Entity
@Table(name = "external_accounts")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MessageChannel platform;

  @Column(nullable = false, length = 100)
  private String externalUserId;

  @Column(length = 100)
  private String displayName;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant linkedAt;
}
