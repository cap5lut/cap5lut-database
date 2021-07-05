package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class ParameterSingleSetterTest {
    ParameterSingleSetter<?> setter = new Batch();
    @BeforeEach
    public void beforeEach() {
        setter = new Batch();
    }

    @AfterEach
    public void afterEach() {
        setter = null;
    }

    @Test
    void addParameter_boolean() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertFalse(invocation.getArgument(1, Boolean.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertTrue(invocation.getArgument(1, Boolean.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setBoolean(any(Integer.class), any(Boolean.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.BOOLEAN, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter(false);
        setter.addParameter(Boolean.TRUE);
        setter.addParameter((Boolean) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_byte() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals((byte) 0, invocation.getArgument(1, Byte.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals((byte) 1, invocation.getArgument(1, Byte.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setByte(any(Integer.class), any(Byte.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.SMALLINT, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter((byte) 0);
        setter.addParameter(Byte.valueOf((byte) 1));
        setter.addParameter((Byte) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_short() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals((short) 0, invocation.getArgument(1, Short.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals((short) 1, invocation.getArgument(1, Short.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setShort(any(Integer.class), any(Short.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.SMALLINT, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter((short) 0);
        setter.addParameter(Short.valueOf((short) 1));
        setter.addParameter((Short) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_integer() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals(0, invocation.getArgument(1, Integer.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals(1, invocation.getArgument(1, Integer.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setInt(any(Integer.class), any(Integer.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.INTEGER, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter(0);
        setter.addParameter(Integer.valueOf(1));
        setter.addParameter((Integer) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_long() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals(0, invocation.getArgument(1, Long.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals(1, invocation.getArgument(1, Long.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setLong(any(Integer.class), any(Long.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.BIGINT, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter(0L);
        setter.addParameter(Long.valueOf(1L));
        setter.addParameter((Long) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_float() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals(0f, invocation.getArgument(1, Float.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals(1f, invocation.getArgument(1, Float.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setFloat(any(Integer.class), any(Float.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.FLOAT, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter(0f);
        setter.addParameter(Float.valueOf(1f));
        setter.addParameter((Float) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_double() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            switch (invocation.getArgument(0, Integer.class)) {
                case 1:
                    assertEquals(0.0, invocation.getArgument(1, Double.class));
                    executionCount.incrementAndGet();
                    break;
                case 2:
                    assertEquals(1.0, invocation.getArgument(1, Double.class));
                    executionCount.incrementAndGet();
                    break;
                default:
                    fail("invalid index");
                    break;
            }
            return null;
        })
                .when(statement)
                .setDouble(any(Integer.class), any(Double.class));
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 3) {
                assertEquals(Types.DOUBLE, invocation.getArgument(1, Integer.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setNull(any(Integer.class), any(Integer.class));

        setter.addParameter(0.0);
        setter.addParameter(Double.valueOf(1.0));
        setter.addParameter((Double) null);
        ((Batch) setter).accept(statement);
        assertEquals(3, executionCount.get());
    }

    @Test
    void addParameter_string() throws SQLException {
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 1) {
                assertEquals("test", invocation.getArgument(1, String.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setString(any(Integer.class), any(String.class));

        setter.addParameter("test");
        ((Batch) setter).accept(statement);
        assertEquals(1, executionCount.get());
    }

    @Test
    void addParameter_instant() throws SQLException {
        final var instant = Instant.now();
        final var executionCount = new AtomicInteger(0);
        final var statement = mock(PreparedStatement.class);
        doAnswer(invocation -> {
            if (invocation.getArgument(0, Integer.class) == 1) {
                assertEquals(Timestamp.from(instant), invocation.getArgument(1, Timestamp.class));
                executionCount.incrementAndGet();
            } else {
                fail("invalid index");
            }
            return null;
        })
                .when(statement)
                .setTimestamp(any(Integer.class), any(Timestamp.class));

        setter.addParameter(instant);
        ((Batch) setter).accept(statement);
        assertEquals(1, executionCount.get());
    }
}