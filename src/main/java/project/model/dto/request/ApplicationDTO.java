package project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import project.model.entity.enum_type.ApplicationStatusEnum;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApplicationDTO {
    @NotBlank(message = "Bạn nên điền mô tả về đơn xin ứng tuyển !")
    private String coverLetter;
    @NotNull(message = "Vui lòng đính kèm file CV của bạn!")
    private MultipartFile cvUrl;
}
