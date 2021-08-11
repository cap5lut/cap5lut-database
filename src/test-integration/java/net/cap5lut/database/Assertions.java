package net.cap5lut.database;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class Assertions {
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrowsWithCause(Class<T> expectedType, Executable action) {
        try {
            action.execute();
            fail("No exception was thrown, expected cause of type " + expectedType);
            return null;
        } catch (Throwable throwable) {
            final var cause = throwable.getCause();
            if (cause == null) {
                fail("Thrown exception had no cause, expected cause of type " + expectedType);
                return null;
            } else if (!expectedType.isInstance(cause)) {
                fail("Expected cause of type " + expectedType + ", but is " + cause);
                return null;
            } else {
                return (T) cause;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable, U extends Throwable> U assertThrowsWithCause(Class<T> expectedOuterType, Class<U> expectedCauseType, Executable action) {
        final var cause = assertThrows(expectedOuterType, action).getCause();
        assertTrue(expectedCauseType.isInstance(cause));
        return (U) cause;
    }

    private Assertions() {
        throw new UnsupportedOperationException();
    }
}
