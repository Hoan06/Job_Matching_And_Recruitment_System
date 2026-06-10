package project.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.model.entity.User;
import project.model.entity.enum_type.RoleEnum;
import project.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeding implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@gmail.com";

        User existingAdmin = userRepository.findByEmailAndActiveTrue(adminEmail);
        if (existingAdmin == null) {
            log.info("Hệ thống chưa có tài khoản ADMIN mặc định. Tiến hành seeding dữ liệu...");

            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(RoleEnum.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Đã khởi tạo thành công tài khoản ADMIN mặc định: {}", adminEmail);
        } else {
            log.info("Tài khoản ADMIN mặc định [{}] đã tồn tại, bỏ qua bước khởi tạo.", adminEmail);
        }
    }
}
