package project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.model.dto.request.*;
import project.model.dto.response.ApiDataResponse;
import project.model.dto.response.JWTResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.RefreshToken;
import project.service.AuthService;
import project.service.RefreshTokenService;
import project.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

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
    @PreAuthorize("isAuthenticated()")
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiDataResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Mã token để reset đã được gửi .",
                authService.forgotPassword(forgotPasswordRequest.getEmail()),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiDataResponse<Boolean>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest , @RequestParam("token") String token) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Khôi phục mật khẩu thành công .",
                authService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getPassword(), token),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<Boolean>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Đổi mật khẩu thành công .",
                authService.changePassword(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword()),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }
}
