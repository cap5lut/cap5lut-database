package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class BatchTest {
    @Test
    void addParameter() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            assertEquals(1, invocation.getArgument(0, Integer.class));
            assertEquals(5, invocation.getArgument(1, Integer.class));
            return null;
        })
                .when(statement)
                .setInt(any(Integer.class), any(Integer.class));

        doAnswer(invocation -> {
            assertEquals(2, invocation.getArgument(0, Integer.class));
            assertEquals("value", invocation.getArgument(1, String.class));
            return null;
        })
                .when(statement)
                .setString(any(Integer.class), any(String.class));

        new Batch().addParameter(5).addParameter("value").accept(statement);
    }
}