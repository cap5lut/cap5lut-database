package net.cap5lut.database;

import net.cap5lut.util.function.BiConsumerEx;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Interface to set single parameters.
 *
 * @param <S> actual statement type.
 */
public interface ParameterSingleSetterStatement<S> {
    /**
     * Adds a parameter setter.
     *
     * @param setter parameter setter
     * @return itself for method chaining
     */
    S addParameter(BiConsumerEx<PreparedStatement, Integer, SQLException> setter);

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(boolean value) {
        return addParameter((row, index) -> row.setBoolean(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Boolean value) {
        return value == null ? addNullParameter(Types.BOOLEAN) : addParameter(value.booleanValue());
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(byte value) {
        return addParameter((row, index) -> row.setByte(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Byte value) {
        return value == null ? addNullParameter(Types.SMALLINT) : addParameter(value.byteValue());
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(short value) {
        return addParameter((row, index) -> row.setShort(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Short value) {
        return value == null ? addNullParameter(Types.SMALLINT) : addParameter(value.shortValue());
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(int value) {
        return addParameter((row, index) -> row.setInt(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Integer value) {
        return value == null ? addNullParameter(Types.INTEGER) : addParameter(value.intValue());
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(int[] value) {
        return addParameter((statement, index) -> {
            final var array = statement
                    .getConnection()
                    .createArrayOf("INT", IntStream.of(value).boxed().toArray());
            statement.setArray(index, array);
        });
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(long value) {
        return addParameter((row, index) -> row.setLong(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Long value) {
        return value == null ? addNullParameter(Types.INTEGER) : addParameter(value.longValue());
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(long[] value) {
        return addParameter((statement, index) -> {
            final var array = statement
                    .getConnection()
                    .createArrayOf("BIGINT", LongStream.of(value).boxed().toArray());
            statement.setArray(index, array);
        });
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(float value) {
        return addParameter((row, index) -> row.setFloat(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(double value) {
        return addParameter((row, index) -> row.setDouble(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(String value) {
        return addParameter((row, index) -> row.setString(index, value));
    }

    /**
     * Adds a parameter.
     *
     * @param value parameter
     * @return itself for method chaining
     */
    default S addParameter(Instant value) {
        return addParameter((row, index) -> row.setTimestamp(index, Timestamp.from(value)));
    }

    /**
     * Adds a {@code NULL} parameter.
     *
     * @param type parameter type
     * @return itself for method chaining
     */
    default S addNullParameter(int type) {
        return addParameter((row, index) -> row.setNull(index, type));
    }
}
