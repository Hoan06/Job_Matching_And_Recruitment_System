package project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.dto.request.BrowseRequest;
import project.model.dto.request.EmailRequest;
import project.model.dto.request.UserDTO;
import project.model.dto.response.ApiDataResponse;
import project.model.dto.response.JobPostingResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.User;
import project.service.JobPostingService;
import project.service.UserService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final JobPostingService jobPostingService;

    @GetMapping("/get-users")
    public ResponseEntity<ApiDataResponse<Page<UserResponse>>> getAllUsers(@RequestParam(value = "page" , defaultValue = "1") Integer page) {
        int size = 3;
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Lấy danh sách tài khoản thành công .",
                userService.getAllHasPage(page,size),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/add-user")
    public ResponseEntity<ApiDataResponse<UserResponse>> addUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Thêm tài khoản thành công .",
                userService.createUser(userDTO),
                null,
                HttpStatus.CREATED
        ) , HttpStatus.CREATED);
    }

    @PostMapping("/find-user")
    public ResponseEntity<ApiDataResponse<UserResponse>> findUser(@Valid @RequestBody EmailRequest emailRequest) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Tìm kiếm tài khoản thành công .",
                userService.findUserByEmail(emailRequest),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/ban-user")
    public ResponseEntity<ApiDataResponse<Boolean>> banUser(@Valid @RequestBody EmailRequest emailRequest) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Khóa tài khoản thành công .",
                userService.banUser(emailRequest),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/get-jobs")
    public ResponseEntity<ApiDataResponse<Page<JobPostingResponse>>> getAllJobs(@RequestParam(value = "page" , defaultValue = "1") Integer page) {
        int size = 3;
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Lấy danh sách tin tuyển dụng thành công .",
                jobPostingService.getAllJobPostings(page,size),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/browse-job/{id}")
    public ResponseEntity<ApiDataResponse<JobPostingResponse>> browseJobPosting(@Valid @RequestBody BrowseRequest browseRequest , @PathVariable("id") Long id) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Cập nhật trạng thái tin tuyển thành công .",
                jobPostingService.browseJobPosting(id,browseRequest),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }
}
