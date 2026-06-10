package project.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.JobStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobPostingUpdateDTO {
    private String title;

    private String description;

    private String salaryRange;

    private JobStatusEnum status;
}