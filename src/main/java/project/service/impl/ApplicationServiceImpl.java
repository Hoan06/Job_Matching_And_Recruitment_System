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
import project.mapper.ApplicationMapper;
import project.model.dto.request.ApplicationDTO;
import project.model.dto.response.ApplicationResponse;
import project.model.dto.response.JobPostingResponse;
import project.model.entity.Application;
import project.model.entity.JobPosting;
import project.model.entity.User;
import project.model.entity.enum_type.ApplicationStatusEnum;
import project.model.entity.enum_type.JobStatusEnum;
import project.repository.ApplicationRepository;
import project.repository.JobPostingRepository;
import project.repository.UserRepository;
import project.service.ApplicationService;
import project.service.JobPostingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    @Override
    public ApplicationResponse applyJob(Long idJob , ApplicationDTO applicationDTO) {
        JobPosting jobPosting = jobPostingRepository.findJobPostingByIdAndStatusEquals(idJob, JobStatusEnum.APPROVED);
        if  (jobPosting == null) {
            throw new BadRequestException("Không tìm thấy tin tuyển bạn muốn ứng tuyển !");
        }

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmailAndActiveTrue(userName);
        if (user == null) {
            log.info("Có lỗi xảy ra không lấy được user đăng nhập !");
            throw new BadRequestException("Có lỗi xảy ra không lấy được user đăng nhập !");
        }

        if (!"application/pdf".equals(applicationDTO.getCvUrl().getContentType())) {
            log.info("Định dạng file không hợp lệ! Hệ thống chỉ chấp nhận file PDF !");
            throw new BadRequestException("Định dạng file không hợp lệ! Hệ thống chỉ chấp nhận file PDF !");
        }

        try {
            String cvUrl = cloudinaryService.uploadPdf(applicationDTO.getCvUrl());

            Application application = Application.builder()
                    .coverLetter(applicationDTO.getCoverLetter())
                    .appliedAt(LocalDateTime.now())
                    .cvUrl(cvUrl)
                    .status(ApplicationStatusEnum.PENDING)
                    .user(user)
                    .jobPosting(jobPosting)
                    .build();

            applicationRepository.save(application);

            return ApplicationResponse.builder()
                    .id(application.getId())
                    .coverLetter(applicationDTO.getCoverLetter())
                    .cvUrl(application.getCvUrl())
                    .appliedAt(application.getAppliedAt())
                    .status(application.getStatus())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xảy ra trong quá trình upload CV: " + e.getMessage());
        }
    }

    @Override
    public ApplicationResponse updateStatusApplication(Long idApplication, ApplicationStatusEnum newApplicationStatusEnum) {
        Application application = applicationRepository.findByIdAndStatusNot(idApplication, ApplicationStatusEnum.REJECTED);
        if (application == null) {
            log.info("Không tìm thấy đơn ứng tuyển muốn duyệt !");
            throw new BadRequestException("Không tìm thấy đơn ứng tuyển muốn duyệt !");
        }

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmailAndActiveTrue(userName);
        if (user == null) {
            log.info("Có lỗi xảy ra không lấy được user đăng nhập !");
            throw new BadRequestException("Có lỗi xảy ra không lấy được user đăng nhập !");
        }

        Long hrOwnerId = application.getJobPosting().getUser().getId();
        Long currentUserId = user.getId();

        if (!currentUserId.equals(hrOwnerId)) {
            log.warn("User [{}] cố tình duyệt đơn ứng tuyển thuộc tin tuyển dụng của HR ID [{}]", userName, hrOwnerId);
            throw new BadRequestException("Bạn không có quyền duyệt đơn ứng tuyển của tin tuyển dụng này!");
        }

        if (newApplicationStatusEnum.ordinal() <= application.getStatus().ordinal()) {
            log.error("Không thể chuyển từ trạng thái {} về trạng thái {} !", application.getStatus(), newApplicationStatusEnum);
            throw new BadRequestException("Trạng thái duyệt đơn không hợp lệ! Bạn chỉ có thể cập nhật tiến lên, không thể lùi về trạng thái trước đó.");
        }


        application.setStatus(newApplicationStatusEnum);
        Application updatedApplication = applicationRepository.save(application);

        return ApplicationResponse.builder()
                .id(updatedApplication.getId())
                .coverLetter(updatedApplication.getCoverLetter())
                .cvUrl(updatedApplication.getCvUrl())
                .appliedAt(updatedApplication.getAppliedAt())
                .status(updatedApplication.getStatus())
                .build();
    }

    @Override
    public Page<ApplicationResponse> getAllApplicationsByUser_Id(Integer page, Integer size) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmailAndActiveTrue(userName);
        if (user == null) {
            log.info("Có lỗi xảy ra không lấy được user đăng nhập !");
            throw new BadRequestException("Có lỗi xảy ra không lấy được user đăng nhập !");
        }

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Application> pageResult = applicationRepository.findApplicationByUser_Id(user.getId(),pageable);
        List<Application> content = pageResult.getContent();
        List<ApplicationResponse> contentResult = content.stream().map(applicationMapper::toApplicationResponse).toList();
        return new PageImpl<>(contentResult, pageResult.getPageable(), pageResult.getTotalElements());
    }
}
