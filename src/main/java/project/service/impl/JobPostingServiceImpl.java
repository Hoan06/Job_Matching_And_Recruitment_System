package project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.exception.BadRequestException;
import project.exception.NotFoundException;
import project.mapper.JobPostingMapper;
import project.model.dto.request.BrowseRequest;
import project.model.dto.request.JobPostingDTO;
import project.model.dto.request.JobPostingUpdateDTO;
import project.model.dto.response.JobPostingResponse;
import project.model.dto.response.UserResponse;
import project.model.entity.JobPosting;
import project.model.entity.User;
import project.model.entity.enum_type.JobStatusEnum;
import project.repository.JobPostingRepository;
import project.repository.UserRepository;
import project.service.JobPostingService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final JobPostingMapper jobPostingMapper;

    @Override
    public JobPostingResponse createJobPosting(JobPostingDTO jobPostingDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmailAndActiveTrue(currentUsername);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy thông tin nhà tuyển dụng đăng nhập");
        }
        JobPosting jobPosting = JobPosting.builder()
                .title(jobPostingDTO.getTitle())
                .description(jobPostingDTO.getDescription())
                .salaryRange(jobPostingDTO.getSalaryRange())
                .status(jobPostingDTO.getStatus())
                .user(user)
                .build();
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return JobPostingResponse.builder()
                .title(savedJobPosting.getTitle())
                .description(savedJobPosting.getDescription())
                .salaryRange(savedJobPosting.getSalaryRange())
                .status(savedJobPosting.getStatus())
                .build();
    }

    @Override
    public JobPostingResponse updateJobPosting(Long idJob, JobPostingUpdateDTO jobPostingDTO) {
        JobPosting existingJob = jobPostingRepository.findById(idJob).orElseThrow(
                () -> new RuntimeException("Không tìm thấy tin tuyển dụng với ID: " + idJob)
        );

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!existingJob.getUser().getEmail().equals(currentUsername)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa tin tuyển dụng này!");
        }

        if (jobPostingDTO.getTitle() != null) {
            existingJob.setTitle(jobPostingDTO.getTitle());
        }
        if (jobPostingDTO.getDescription() != null) {
            existingJob.setDescription(jobPostingDTO.getDescription());
        }
        if (jobPostingDTO.getSalaryRange() != null) {
            existingJob.setSalaryRange(jobPostingDTO.getSalaryRange());
        }

        if (jobPostingDTO.getStatus() != null) {
            JobStatusEnum currentStatus = existingJob.getStatus();
            JobStatusEnum newStatus = jobPostingDTO.getStatus();

            if (currentStatus != newStatus) {
                if (currentStatus == JobStatusEnum.PENDING_APPROVAL) {
                    throw new RuntimeException("Tin tuyển dụng đang chờ phê duyệt, bạn không thể thay đổi trạng thái!");
                }
                if (currentStatus == JobStatusEnum.APPROVED && newStatus != JobStatusEnum.CLOSED) {
                    throw new RuntimeException("Tin đang hiển thị, bạn chỉ được phép Đóng tin (CLOSED)!");
                }
                if ((currentStatus == JobStatusEnum.DRAFT || currentStatus == JobStatusEnum.REJECTED)
                        && (newStatus != JobStatusEnum.DRAFT && newStatus != JobStatusEnum.PENDING_APPROVAL)) {
                    throw new RuntimeException("Hành động không hợp lệ! Bạn chỉ có thể Lưu nháp hoặc Gửi duyệt tin này.");
                }
                if (currentStatus == JobStatusEnum.CLOSED && newStatus == JobStatusEnum.APPROVED) {
                    throw new RuntimeException("Bạn không thể tự ý kích hoạt lại tin đã đóng, vui lòng chuyển về Chờ phê duyệt!");
                }
                existingJob.setStatus(newStatus);
            }
        }

        JobPosting updatedJob = jobPostingRepository.save(existingJob);

        return JobPostingResponse.builder()
                .title(updatedJob.getTitle())
                .description(updatedJob.getDescription())
                .salaryRange(updatedJob.getSalaryRange())
                .status(updatedJob.getStatus())
                .build();
    }

    @Override
    public Page<JobPostingResponse> getAllJobPostings(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<JobPosting> pageResult = jobPostingRepository.findAll(pageable);
        List<JobPosting> content = pageResult.getContent();
        List<JobPostingResponse> contentResult = content.stream().map(jobPostingMapper::mapToJobPostingResponse).toList();
        return new PageImpl<>(contentResult, pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public JobPostingResponse browseJobPosting(Long idJob , BrowseRequest browseRequest) {
        Optional<JobPosting> jobPosting = jobPostingRepository.findById(idJob);
        if (jobPosting.isEmpty()) {
            log.info("Không tìm thấy tin tuyển muốn duyệt !");
            throw new NotFoundException("Không tìm thấy tin tuyển muốn duyệt !");
        }
        if (jobPosting.get().getStatus().equals(JobStatusEnum.CLOSED)) {
            log.info("Tin này đã bị đóng không thể duyệt !");
            throw new BadRequestException("Tin này đã bị đóng không thể duyệt !");
        }
        if (jobPosting.get().getStatus().equals(JobStatusEnum.APPROVED)) {
            log.info("Tin này đã được duyệt từ trước rồi !");
            throw new BadRequestException("Tin này đã được duyệt từ trước rồi !");
        }
        JobStatusEnum requestStatus = browseRequest.getStatus();

        if (requestStatus.equals(JobStatusEnum.REJECTED)) {
            jobPosting.get().setStatus(JobStatusEnum.REJECTED);
        } else if (requestStatus.equals(JobStatusEnum.APPROVED)) {
            jobPosting.get().setStatus(JobStatusEnum.APPROVED);
        } else {
            throw new BadRequestException("Trạng thái phê duyệt không hợp lệ!");
        }
        JobPosting jobPosting1 = jobPostingRepository.save(jobPosting.get());
        return jobPostingMapper.mapToJobPostingResponse(jobPosting1);
    }
}
