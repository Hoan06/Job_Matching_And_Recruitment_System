package project.service;

import org.springframework.data.domain.Page;
import project.model.dto.request.BrowseRequest;
import project.model.dto.request.JobPostingDTO;
import project.model.dto.request.JobPostingUpdateDTO;
import project.model.dto.response.JobPostingResponse;
import project.model.entity.JobPosting;

public interface JobPostingService {
    JobPostingResponse createJobPosting(JobPostingDTO jobPostingDTO);
    JobPostingResponse updateJobPosting(Long idJob, JobPostingUpdateDTO jobPostingDTO);
    Page<JobPostingResponse> getAllJobPostings(Integer page, Integer size);
    JobPostingResponse browseJobPosting(Long idJob , BrowseRequest  browseRequest);
    Page<JobPostingResponse> findByTitle(String title, Integer page, Integer size);

}
