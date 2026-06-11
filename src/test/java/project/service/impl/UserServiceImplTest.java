package project.service.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.exception.BadRequestException;
import project.mapper.UserMapper;
import project.model.dto.request.LoginDTO;
import project.model.dto.request.LogoutRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.JWTResponse;
import project.model.entity.User;
import project.model.entity.enum_type.RoleEnum;
import project.repository.UserRepository;
import project.security.jwt.JWTProvider;
import project.security.principle.CustomUserDetails;
import project.service.RefreshTokenService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RedisBlacklistService redisBlacklistService;
    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@gmail.com")
                .passwordHash("hashed_password")
                .role(RoleEnum.CANDIDATE)
                .active(true)
                .build();

        userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123456");
        userDTO.setRole(RoleEnum.CANDIDATE);
    }

    @Test
    @DisplayName("Login - Thành công khi thông tin chính xác")
    void login_Success() {
        LoginDTO loginDTO = new LoginDTO("test@gmail.com", "123456");
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        project.model.entity.RefreshToken mockRefreshToken = new project.model.entity.RefreshToken();
        mockRefreshToken.setRefreshToken("mock-refresh-token");

        when(userRepository.findByEmailAndActiveTrue(loginDTO.getEmail())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@gmail.com");
        when(jwtProvider.generateToken("test@gmail.com")).thenReturn("mock-access-token");
        when(refreshTokenService.createRefreshToken("test@gmail.com")).thenReturn(mockRefreshToken);

        // When
        JWTResponse response = userService.login(loginDTO);

        // Then
        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        verify(userRepository, times(1)).findByEmailAndActiveTrue(loginDTO.getEmail());
    }

    @Test
    @DisplayName("Login - Thất bại ném lỗi BadRequest khi tài khoản bị khóa/không tìm thấy")
    void login_ThrowsBadRequestException_WhenUserInactive() {
        // Given
        LoginDTO loginDTO = new LoginDTO("locked@gmail.com", "123456");
        when(userRepository.findByEmailAndActiveTrue(loginDTO.getEmail())).thenReturn(null);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.login(loginDTO);
        });

        assertEquals("Tài khoản đã bị khóa không thể đăng nhập !", exception.getMessage());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Create User - Thất bại ném lỗi BadRequest khi cố tình tạo tài khoản ADMIN")
    void createUser_ThrowsBadRequestException_WhenRoleIsAdmin() {
        // Given
        userDTO.setRole(RoleEnum.ADMIN);

        // Giả lập khi gọi passwordEncoder.encode thì trả về chuỗi bất kỳ, tránh bị NullPointerException
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(userDTO);
        });

        assertEquals("Không thể thêm tài khoản admin !", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Logout - Thành công đưa token vào Blacklist")
    void logout_Success() {
        // Given
        String accessToken = "Bearer valid-token-123";
        String pureToken = "valid-token-123";
        LogoutRequest logoutRequest = new LogoutRequest("refresh-token-123");

        when(redisBlacklistService.isCheckBlacklist(pureToken)).thenReturn(false);
        when(jwtProvider.getEmailFromToken(pureToken)).thenReturn("test@gmail.com");
        when(userRepository.findByEmailAndActiveTrue("test@gmail.com")).thenReturn(user);

        // Giả lập thời gian hết hạn của token là trong tương lai (còn 60.000 ms)
        LocalDateTime futureExpiration = LocalDateTime.now().plusMinutes(1);
        when(jwtProvider.getExpirationDateFromToken(pureToken)).thenReturn(futureExpiration);

        // When
        boolean result = userService.logout(logoutRequest, accessToken);

        // Then
        assertTrue(result);
        verify(refreshTokenService, times(1)).revokeRefreshToken("refresh-token-123");
        verify(redisBlacklistService, times(1)).blacklistToken(eq(pureToken), anyLong());
    }

    @Test
    @DisplayName("Logout - Thất bại ném lỗi IllegalArgumentException khi Token thiếu Bearer")
    void logout_ThrowsIllegalArgumentException_WhenTokenInvalid() {
        // Given
        String invalidAccessToken = "InvalidTokenWithoutBearer";
        LogoutRequest logoutRequest = new LogoutRequest("refresh-token-123");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.logout(logoutRequest, invalidAccessToken);
        });

        assertEquals("Access Token không hợp lệ hoặc bị thiếu !", exception.getMessage());
        verify(redisBlacklistService, never()).isCheckBlacklist(any());
        verify(refreshTokenService, never()).revokeRefreshToken(any());
    }
}