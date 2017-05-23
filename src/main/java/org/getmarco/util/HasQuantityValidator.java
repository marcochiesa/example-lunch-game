package org.getmarco.util;

import io.bretty.console.view.Validator;

/**
 * Validation for user input requiring a positive {@link java.lang.Integer integer}
 * greater than 0.
 */
public class HasQuantityValidator implements Validator<Integer> {
    @Override
    public boolean isValid(Integer integer) {
        // Require positive integer greater than or equal to 1
        return integer >= 1;
    }
}
