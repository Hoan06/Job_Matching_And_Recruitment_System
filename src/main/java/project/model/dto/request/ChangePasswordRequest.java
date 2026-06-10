package project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "Không được để trống mật khẩu cũ !")
    private String oldPassword;
    @NotBlank(message = "Không được để trống mật khẩu mới !")
    private String newPassword;
}
