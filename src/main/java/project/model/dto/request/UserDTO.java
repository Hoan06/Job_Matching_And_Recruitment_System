package project.model.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.custom_valid.ValidEmail;
import project.custom_valid.ValidRoleUser;
import project.model.entity.enum_type.RoleEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    @NotBlank(message = "Email không được để trống !")
    @Pattern(
            regexp = "^[a-zA-A0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "Định dạng email không hợp lệ (Ví dụ: example@gmail.com)!"
    )
    @ValidEmail
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống !")
    private String passwordHash;
    @NotNull(message = "Vui lòng chọn role !")
    @ValidRoleUser
    private RoleEnum role;
}
