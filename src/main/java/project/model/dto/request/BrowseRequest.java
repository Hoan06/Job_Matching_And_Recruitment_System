package project.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.JobStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BrowseRequest {
    @NotNull(message = "Không được để trống trạng thái !")
    private JobStatusEnum status;
}
