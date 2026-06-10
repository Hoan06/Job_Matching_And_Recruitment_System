package project.mapper;

import org.springframework.stereotype.Component;
import project.model.dto.response.JobPostingResponse;
import project.model.entity.JobPosting;

@Component
public class JobPostingMapper {
    public JobPostingResponse mapToJobPostingResponse(JobPosting jobPosting) {
        return JobPostingResponse.builder()
                .jobId(jobPosting.getId())
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .status(jobPosting.getStatus())
                .salaryRange(jobPosting.getSalaryRange())
                .build();
    }
}
