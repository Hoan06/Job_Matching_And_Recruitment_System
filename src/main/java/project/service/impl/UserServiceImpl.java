package project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.exception.BadRequestException;
import project.exception.NotFoundException;
import project.mapper.UserMapper;
import project.model.dto.request.EmailRequest;
import project.model.dto.request.LoginDTO;
import project.model.dto.request.LogoutRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.JWTResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.TokenBlacklist;
import project.model.entity.User;
import project.model.entity.enum_type.RoleEnum;
import project.repository.RefreshTokenRepository;
import project.repository.TokenBlackListRepository;
import project.repository.UserRepository;
import project.security.jwt.JWTProvider;
import project.security.principle.CustomUserDetails;
import project.service.RefreshTokenService;
import project.service.UserService;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final RefreshTokenService  refreshTokenService;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final UserMapper userMapper;
    private final RedisBlacklistService  redisBlacklistService;

    @Override
    @Transactional
    public UserResponse register(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.getEmail())
                .passwordHash(passwordEncoder.encode(userDTO.getPassword()))
                .role(userDTO.getRole())
                .active(true)
                .build();
        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .active(savedUser.isActive())
                .build();
    }

    @Override
    @Transactional
    public JWTResponse login(LoginDTO userLoginDTO) {
        try {
            User user = userRepository.findByEmailAndActiveTrue(userLoginDTO.getEmail());
            if (user == null) {
                log.info("Tài khoản đã bị khóa không thể đăng nhập !");
                throw new BadRequestException("Tài khoản đã bị khóa không thể đăng nhập !");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
            );
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtProvider.generateToken(customUserDetails.getUsername());

            String refreshTokenValue = refreshTokenService.createRefreshToken(customUserDetails.getUsername()).getRefreshToken();

            return JWTResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshTokenValue)
                    .build();
        } catch (AuthenticationException e) {
            log.info("Sai email hoặc password !");
            throw new BadCredentialsException("Sai email or password !");
        }
    }

    @Override
    public List<User> getAlls() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean logout(LogoutRequest logoutRequest, String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty() || !accessToken.startsWith("Bearer ")) {
            log.info("Access Token không hợp lệ hoặc bị thiếu !");
            throw new IllegalArgumentException("Access Token không hợp lệ hoặc bị thiếu !");
        }

        String token = accessToken.substring(7).trim();

        if (token.isEmpty()) {
            log.info("Token trống không hợp lệ!");
            throw new IllegalArgumentException("Token trống không hợp lệ!");
        }
        if (redisBlacklistService.isCheckBlacklist(token)) {
            log.info("Token này đã đăng xuất từ trước đó!");
            throw new RuntimeException("Token này đã đăng xuất từ trước đó!");
        }

        refreshTokenService.revokeRefreshToken(logoutRequest.getRefreshToken());
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmailAndActiveTrue(email);
        if (user == null) {
            log.info("User không tồn tại hoặc đã bị khóa !");
            throw new RuntimeException("User không tồn tại hoặc đã bị khóa !");
        }

        LocalDateTime expirationDate = jwtProvider.getExpirationDateFromToken(token);

        long remainingTimeMillis = java.time.Duration.between(LocalDateTime.now(), expirationDate).toMillis();

        log.info("Thời gian còn lại của token (ms): {}", remainingTimeMillis);

        if (remainingTimeMillis > 0) {
            redisBlacklistService.blacklistToken(token, remainingTimeMillis);
            log.info("Đã lưu token vào Redis Blacklist thành công.");
        } else {
            log.warn("Token đã hết hạn từ trước, không đưa vào Redis.");
        }

        log.info("User {} đã đăng xuất thành công và vô hiệu hóa Token.", email);
        return true;
    }

    @Override
    public Page<UserResponse> getAllHasPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<User> pageResult = userRepository.findAllByActiveTrue(pageable);
        List<User> content = pageResult.getContent();
        List<UserResponse> contentResult = content.stream().map(userMapper::mapToUserResponse).toList();
        return new PageImpl<>(contentResult, pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    @Transactional
    public UserResponse createUser(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.getEmail())
                .passwordHash(passwordEncoder.encode(userDTO.getPassword()))
                .role(userDTO.getRole())
                .active(true)
                .build();
        if (user.getRole() == RoleEnum.ADMIN) {
            log.info("Không thể thêm tài khoản admin !");
            throw new BadRequestException("Không thể thêm tài khoản admin !");
        }
        User savedUser = userRepository.save(user);
        return userMapper.mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse findUserByEmail(EmailRequest email) {
        User user = userRepository.findByEmailAndActiveTrue(email.getEmail());
        if (user == null) {
            log.info("Không tìm thấy user !");
            throw new NotFoundException("Không tìm thấy user có email : " + email);
        }
        return userMapper.mapToUserResponse(user);
    }

    @Override
    @Transactional
    public boolean banUser(EmailRequest email) {
        User user = userRepository.findByEmailAndActiveTrue(email.getEmail());
        if (user == null) {
            log.info("Không tìm thấy user để xóa !");
            throw new NotFoundException("Không tìm thấy user có email : " + email);
        }
        if (user.getRole() == RoleEnum.ADMIN) {
            log.info("Không thể khóa tài khoản admin !");
            throw new BadRequestException("Không thể khóa tài khoản admin !");
        }
        user.setActive(false);
        userRepository.save(user);
        return true;
    }
}
