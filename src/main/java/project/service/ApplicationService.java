package project.service;

import org.springframework.data.domain.Page;
import project.model.dto.request.ApplicationDTO;
import project.model.dto.response.ApplicationResponse;
import project.model.entity.enum_type.ApplicationStatusEnum;

public interface ApplicationService {
    ApplicationResponse applyJob(Long idJob , ApplicationDTO applicationDTO);
    ApplicationResponse updateStatusApplication(Long idApplication , ApplicationStatusEnum newApplicationStatusEnum);
    Page<ApplicationResponse> getAllApplicationsByUser_Id(Integer page, Integer size);
}
