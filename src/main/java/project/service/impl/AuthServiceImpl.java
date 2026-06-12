package project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.exception.BadRequestException;
import project.model.entity.User;
import project.repository.UserRepository;
import project.security.jwt.JWTProvider;
import project.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    @Override
    public Object forgotPassword(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email);
        if (user == null) {
            return null;
        }
        return jwtProvider.generateResetPasswordToken(email);
    }

    @Override
    @Transactional
    public boolean resetPassword(String email, String password, String token) {
        jwtProvider.validateResetToken(token);
        User user = userRepository.findByEmailAndActiveTrue(email);
        if (user == null) {
            log.info("Không tìm thấy tài khoản muốn reset mật khẩu !");
            throw new BadRequestException("Không tìm thấy tài khoản muốn reset mật khẩu !");
        }
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean changePassword(String oldPassword, String newPassword) {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        User user = userRepository.findByEmailAndActiveTrue(email);
        if (user == null) {
            log.info("Không tìm được tài khoản bạn muốn đổi mật khẩu !");
            throw new BadRequestException("Không tìm được tài khoản bạn muốn đổi mật khẩu !");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            log.info("Mật khẩu cũ không chính xác !");
            throw new BadRequestException("Mật khẩu cũ không chính xác !");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
