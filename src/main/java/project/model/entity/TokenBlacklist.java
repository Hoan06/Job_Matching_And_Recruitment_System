package project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklists")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_blacklist_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String tokenValue;
    @Column(nullable = false , name = "revoked_at")
    private LocalDateTime revokedAt;
    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
