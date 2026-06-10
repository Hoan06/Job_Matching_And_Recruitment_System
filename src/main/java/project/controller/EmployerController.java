package project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.dto.request.JobPostingDTO;
import project.model.dto.request.JobPostingUpdateDTO;
import project.model.dto.request.UpdateStatusApplicationRequest;
import project.model.dto.response.ApiDataResponse;
import project.model.dto.response.ApplicationResponse;
import project.model.dto.response.JobPostingResponse;
import project.service.ApplicationService;
import project.service.JobPostingService;

@RestController
@RequestMapping("/api/v1/employer")
@RequiredArgsConstructor
public class EmployerController {
    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;

    @PostMapping("/up-jobPosting")
    public ResponseEntity<ApiDataResponse<JobPostingResponse>> upJobPosting(@Valid @RequestBody JobPostingDTO jobPostingDTO) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Thêm tin tuyển dụng thành công ( chờ phê duyệt ) !",
                jobPostingService.createJobPosting(jobPostingDTO),
                null,
                HttpStatus.CREATED
        ) , HttpStatus.CREATED);
    }

    @PutMapping("/update-jobPosting/{idJob}")
    public ResponseEntity<ApiDataResponse<JobPostingResponse>> updateJobPosting(@Valid @RequestBody JobPostingUpdateDTO jobPostingDTO ,
                                                                                @PathVariable long idJob) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Cập nhật toàn bộ tin thành công !",
                jobPostingService.updateJobPosting(idJob,jobPostingDTO),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PatchMapping("/update-jobPosting/{idJob}")
    public ResponseEntity<ApiDataResponse<JobPostingResponse>> updatePatchJobPosting(@Valid @RequestBody JobPostingUpdateDTO jobPostingDTO ,
                                                                                @PathVariable long idJob) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Cập nhật tin thành công !",
                jobPostingService.updateJobPosting(idJob,jobPostingDTO),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }

    @PostMapping("/update-statusApplication/{idApp}")
    public ResponseEntity<ApiDataResponse<ApplicationResponse>> updateStatusApplication(@Valid @RequestBody UpdateStatusApplicationRequest updateStatusApplicationRequest ,
                                                                                        @PathVariable long idApp) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Cập nhật trạng thái hồ sơ thành công .",
                applicationService.updateStatusApplication(idApp,updateStatusApplicationRequest.getApplicationStatus()),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }
}
