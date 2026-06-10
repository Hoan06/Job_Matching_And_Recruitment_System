package project.service;

import project.model.dto.request.RefreshTokenRequest;
import project.model.dto.response.JWTResponse;
import project.model.entity.RefreshToken;

import java.time.Instant;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyRefreshToken(RefreshToken refreshToken);
    JWTResponse refreshToken(RefreshTokenRequest refreshToken);
    void revokeRefreshToken(String refreshToken);
}
