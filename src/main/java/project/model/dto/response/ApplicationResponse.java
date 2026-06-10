package project.model.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.ApplicationStatusEnum;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private String coverLetter;
    private String cvUrl;
    private LocalDateTime appliedAt;
    private ApplicationStatusEnum status;
}
