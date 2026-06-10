package project.mapper;

import org.springframework.stereotype.Component;
import project.model.dto.request.UserDTO;
import project.model.dto.response.UserResponse;
import project.model.entity.User;

@Component
public class UserMapper {
    public User mapToUser(UserDTO userDTO) {
        return User.builder()
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .passwordHash(userDTO.getPasswordHash())
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}
