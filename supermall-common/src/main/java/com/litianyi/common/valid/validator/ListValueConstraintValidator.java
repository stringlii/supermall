package com.litianyi.common.valid.validator;

import com.litianyi.common.valid.annotation.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/11 4:27 PM
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Number> {

    private final Set<Number> set = new HashSet<>();

    @Override
    public void initialize(ListValue value) {
        for (int i : value.value()) {
            set.add(i);
        }
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
