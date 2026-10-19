package br.com.greencontrol.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/** Limite conservador em bytes UTF-8 para VARCHAR2(n BYTE) do schema legado. */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OracleTextValidator.class)
public @interface OracleText {
    int max();
    String message() default "Texto excede o limite de bytes do Oracle.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
