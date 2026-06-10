package project.custom_valid.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import project.custom_valid.ValidEmail;
import project.model.entity.User;
import project.repository.UserRepository;

@RequiredArgsConstructor
public class ValidEmailImpl implements ConstraintValidator<ValidEmail, String> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        User user = userRepository.findByEmailAndActiveTrue(value);
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return user == null;
    }
}
