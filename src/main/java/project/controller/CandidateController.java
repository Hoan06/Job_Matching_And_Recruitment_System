package project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.dto.request.ApplicationDTO;
import project.model.dto.response.ApiDataResponse;
import project.model.dto.response.ApplicationResponse;
import project.model.dto.response.JobPostingResponse;
import project.service.ApplicationService;
import project.service.JobPostingService;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
public class CandidateController {
    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;

    @GetMapping("/find-jobs")
    public ResponseEntity<ApiDataResponse<Page<JobPostingResponse>>> findJobs(@RequestParam(value = "page" , defaultValue = "1") Integer page ,
                                                                              @RequestParam(value = "title" , defaultValue = "") String title) {
        int size = 3;
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Tìm kiếm job thành công .",
                jobPostingService.findByTitle(title,page,size),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/apply-job/{idJob}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiDataResponse<ApplicationResponse>> applyJob(@Valid @ModelAttribute ApplicationDTO applicationDTO ,
                                                                         @PathVariable("idJob") Long idJob) {
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Nộp đơn ứng tuyển thành công .",
                applicationService.applyJob(idJob,applicationDTO),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<ApiDataResponse<Page<ApplicationResponse>>> getMyApplications(@RequestParam(value = "page" , defaultValue = "1") Integer page){
        int size = 3;
        return new ResponseEntity<>(new ApiDataResponse<>(
                true,
                "Lấy lịch sử ứng tuyển thành công .",
                applicationService.getAllApplicationsByUser_Id(page,size),
                null,
                HttpStatus.OK
        ) , HttpStatus.OK);
    }
}
