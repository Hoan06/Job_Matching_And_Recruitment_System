package project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.dto.response.JobPostingResponse;
import project.model.entity.JobPosting;
import project.model.entity.enum_type.JobStatusEnum;

import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    Page<JobPosting> findAllByTitleContainingAndStatusEquals(String title, Pageable pageable , JobStatusEnum jobStatusEnum);
    JobPosting findJobPostingByIdAndStatusEquals(Long id, JobStatusEnum status);
}
