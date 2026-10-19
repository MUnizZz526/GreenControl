package br.com.greencontrol.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

public class OracleTextValidator implements ConstraintValidator<OracleText, String> {
    private int max;
    @Override public void initialize(OracleText constraint) { max=constraint.max(); }
    @Override public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.getBytes(StandardCharsets.UTF_8).length <= max;
    }
}
