package org.getmarco.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.bretty.console.view.Validator;

/**
 * Validation for user input requiring a string that doesn't belong to a
 * {@link java.util.Collection collection} of existing names.
 */
public class UniqueNameValidator implements Validator<String> {

    // Set of names to check against
    private Set<String> names;

    public UniqueNameValidator(Collection<String> names) {
        if (names == null)
            throw new IllegalArgumentException("null names");

        this.names = new HashSet<>(names);
    }

    @Override
    public boolean isValid(String name) {
        // given name should not exist in set of names
        return !names.contains(name);
    }
}
