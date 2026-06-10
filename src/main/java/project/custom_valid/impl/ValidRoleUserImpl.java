package project.custom_valid.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.custom_valid.ValidRoleUser;
import project.model.entity.enum_type.RoleEnum;

public class ValidRoleUserImpl implements ConstraintValidator<ValidRoleUser, RoleEnum> {
    @Override
    public boolean isValid(RoleEnum value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value != RoleEnum.ADMIN;
    }
}
