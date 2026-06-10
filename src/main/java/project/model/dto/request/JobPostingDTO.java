package project.model.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.custom_valid.ValidJobStatusEnum;
import project.model.entity.User;
import project.model.entity.enum_type.JobStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobPostingDTO {
    @NotBlank(message = "Không được để trống tiêu đề tuyển dụng !")
    private String title;
    @NotBlank(message = "Không được để trống mô tả !")
    private String description;
    private String salaryRange;
    @NotNull(message = "Không được để trống trạng thái !")
    @ValidJobStatusEnum
    private JobStatusEnum status;
}
