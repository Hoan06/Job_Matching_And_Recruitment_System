package project.mapper;

import org.springframework.stereotype.Component;
import project.model.dto.response.ApplicationResponse;
import project.model.entity.Application;

@Component
public class ApplicationMapper {
    public ApplicationResponse toApplicationResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .coverLetter(application.getCoverLetter())
                .appliedAt(application.getAppliedAt())
                .cvUrl(application.getCvUrl())
                .status(application.getStatus())
                .build();
    }
}
