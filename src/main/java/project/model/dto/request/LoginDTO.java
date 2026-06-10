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
public class LoginDTO {
    @NotBlank(message = "Không được để trống email !")
    private String email;
    @NotBlank(message = "Không được để trống password !")
    private String password;
}
