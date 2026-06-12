package project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.model.dto.request.RefreshTokenRequest;
import project.model.dto.response.JWTResponse;
import project.model.entity.RefreshToken;
import project.repository.RefreshTokenRepository;
import project.security.jwt.JWTProvider;
import project.security.principle.CustomUserDetails;
import project.service.RefreshTokenService;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final JWTProvider jwtProvider;

    @Value("${jwt-refresh-expired}")
    private Long jwtRefreshExpired;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .expiryDate(Instant.now().plusMillis(jwtRefreshExpired))
                .refreshToken(UUID.randomUUID().toString())
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyRefreshToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh token đã hết hạn");
        }
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public JWTResponse refreshToken(RefreshTokenRequest refreshToken) {
        String token = refreshToken.getRefreshToken();
        RefreshToken refreshToken1 = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Không tồn tại exception này !"));

        verifyRefreshToken(refreshToken1);

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(refreshToken1.getEmail());
        String newAccessToken = jwtProvider.generateToken(userDetails.getUsername());

        return JWTResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken1.getRefreshToken())
                .build();
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        RefreshToken refreshToken1 = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new RuntimeException("Refresh Token không tồn tại !")
        );
        verifyRefreshToken(refreshToken1);
        refreshToken1.setRevoked(true);
        refreshTokenRepository.save(refreshToken1);
    }
}
