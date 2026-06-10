package project.custom_valid.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.custom_valid.ValidJobStatusEnum;
import project.model.entity.enum_type.JobStatusEnum;

public class ValidJobStatusEnumImpl implements ConstraintValidator<ValidJobStatusEnum, JobStatusEnum> {
    @Override
    public boolean isValid(JobStatusEnum value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value == JobStatusEnum.DRAFT ||  value == JobStatusEnum.PENDING_APPROVAL;
    }
}
