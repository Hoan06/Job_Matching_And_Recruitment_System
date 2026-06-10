package project.model.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.User;
import project.model.entity.enum_type.JobStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobPostingResponse {
    private Long jobId;
    private String title;
    private String description;
    private String salaryRange;
    private JobStatusEnum status;
}
