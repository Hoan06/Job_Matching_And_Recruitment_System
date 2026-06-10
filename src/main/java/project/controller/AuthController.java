package project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.dto.request.LoginDTO;
import project.model.dto.request.LogoutRequest;
import project.model.dto.request.RefreshTokenRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.ApiDataResponse;
import project.model.dto.response.JWTResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.RefreshToken;
import project.service.RefreshTokenService;
import project.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiDataResponse<UserResponse>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Đăng kí tài khoản " + userDTO.getEmail() + " thành công .",
                userService.register(userDTO),
                null,
                HttpStatus.CREATED
        ) , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<JWTResponse>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Đăng nhập thành công .",
                userService.login(loginDTO),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiDataResponse<JWTResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Lấy token mới thành công .",
                refreshTokenService.refreshToken(refreshTokenRequest),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiDataResponse<Boolean>> logout(@Valid @RequestBody LogoutRequest logoutRequest ,
                                                          @RequestHeader("Authorization") String accessToken) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Đăng xuất thành công .",
                userService.logout(logoutRequest,accessToken),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }
}
