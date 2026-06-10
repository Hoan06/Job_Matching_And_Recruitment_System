package project.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.ApplicationStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateStatusApplicationRequest {
    @NotNull(message = "Vui lòng nhập trạng thái mới !")
    private ApplicationStatusEnum applicationStatus;
}
