package project.service;

import org.springframework.data.domain.Page;
import project.model.dto.request.EmailRequest;
import project.model.dto.request.LoginDTO;
import project.model.dto.request.LogoutRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.JWTResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.User;

import java.util.List;

public interface UserService {
    UserResponse register(UserDTO userDTO);
    JWTResponse login(LoginDTO userLoginDTO);
    List<User> getAlls();
    boolean logout(LogoutRequest logoutRequest, String accessToken);
    Page<UserResponse> getAllHasPage(Integer page, Integer size);
    UserResponse createUser(UserDTO userDTO);
    UserResponse findUserByEmail(EmailRequest email);
    boolean banUser(EmailRequest email);
}
