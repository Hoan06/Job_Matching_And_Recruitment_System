package project.custom_valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import project.custom_valid.impl.ValidEmailImpl;
import project.custom_valid.impl.ValidRoleUserImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {ValidRoleUserImpl.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidRoleUser {
    String message() default "Bạn chỉ có thể chọn ( EMPLOYER OR CANDIDATE ) trong hệ thống !";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
