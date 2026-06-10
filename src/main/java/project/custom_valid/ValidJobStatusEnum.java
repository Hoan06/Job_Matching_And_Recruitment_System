package project.custom_valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import project.custom_valid.impl.ValidEmailImpl;
import project.custom_valid.impl.ValidJobStatusEnumImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {ValidJobStatusEnumImpl.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidJobStatusEnum {
    String message() default "Bạn chỉ được chọn giữa 2 trạng thái tin ( DRAFT , PENDING_APPROVAL) !";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
