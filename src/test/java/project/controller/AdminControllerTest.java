package project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import project.model.dto.request.BrowseRequest;
import project.model.dto.request.EmailRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.JobPostingResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.enum_type.JobStatusEnum;
import project.model.entity.enum_type.RoleEnum;
import project.service.JobPostingService;
import project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JobPostingService jobPostingService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ControllerAdvice
    static class TestBinderAdvice {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            // Thiết lập validator trống để bỏ qua các Custom Validator dính UserRepository
            binder.setValidator(new org.springframework.validation.Validator() {
                @Override
                public boolean supports(Class<?> clazz) {
                    return true;
                }
                @Override
                public void validate(Object target, org.springframework.validation.Errors errors) {
                }
            });
        }
    }

    @BeforeEach
    void setUp() {
        // Dựng MockMvc kết hợp với bộ xóa validation để giữ Mockito độc lập không gây UnnecessaryStubbingException
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new TestBinderAdvice())
                .build();
    }

    @Test
    @DisplayName("GET /get-users - Thành công trả về danh sách phân trang")
    void getAllUsers_Success() throws Exception {
        // Given
        UserResponse userResponse = UserResponse.builder()
                .email("admin@gmail.com")
                .role(RoleEnum.ADMIN)
                .active(true)
                .build();
        Page<UserResponse> mockPage = new PageImpl<>(Collections.singletonList(userResponse), PageRequest.of(0, 3), 1);

        when(userService.getAllHasPage(1, 3)).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/get-users")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách tài khoản thành công ."))
                .andExpect(jsonPath("$.data.content[0].email").value("admin@gmail.com"));
    }

    @Test
    @DisplayName("POST /add-user - Thành công tạo user mới và trả về HttpStatus 201")
    void addUser_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newuser@gmail.com");
        userDTO.setPassword("password123");
        userDTO.setRole(RoleEnum.ADMIN);

        UserResponse userResponse = UserResponse.builder()
                .email("newuser@gmail.com")
                .role(RoleEnum.ADMIN)
                .active(true)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Thêm tài khoản thành công ."))
                .andExpect(jsonPath("$.data.email").value("newuser@gmail.com"));
    }

    @Test
    @DisplayName("POST /find-user - Thành công tìm kiếm user theo email")
    void findUser_Success() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("findme@gmail.com");

        UserResponse userResponse = UserResponse.builder()
                .email("findme@gmail.com")
                .role(RoleEnum.ADMIN)
                .active(true)
                .build();

        when(userService.findUserByEmail(any(EmailRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/find-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tìm kiếm tài khoản thành công ."))
                .andExpect(jsonPath("$.data.email").value("findme@gmail.com"));
    }

    @Test
    @DisplayName("POST /ban-user - Thành công khóa tài khoản người dùng")
    void banUser_Success() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("badboy@gmail.com");

        when(userService.banUser(any(EmailRequest.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/ban-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Khóa tài khoản thành công ."))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("POST /browse-job/{id} - Thành công cập nhật trạng thái bài đăng")
    void browseJobPosting_Success() throws Exception {
        // Given
        Long jobId = 99L;

        BrowseRequest browseRequest = new BrowseRequest();
        browseRequest.setStatus(JobStatusEnum.APPROVED);

        JobPostingResponse jobResponse = new JobPostingResponse();

        when(jobPostingService.browseJobPosting(eq(jobId), any(BrowseRequest.class))).thenReturn(jobResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/browse-job/{id}", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(browseRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cập nhật trạng thái tin tuyển thành công ."));
    }
}