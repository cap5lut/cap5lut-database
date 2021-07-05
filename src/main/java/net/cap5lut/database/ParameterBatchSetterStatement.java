package net.cap5lut.database;

import java.sql.PreparedStatement;

/**
 * Interface to set parameters in batches.
 *
 * @param <S> actual statement type.
 */
public interface ParameterBatchSetterStatement<S> {
    /**
     * Adds a batch setter.
     *
     * @param setter batch setter
     * @return itself for method chaining
     */
    S add(SQLConsumer<PreparedStatement> setter);
}
