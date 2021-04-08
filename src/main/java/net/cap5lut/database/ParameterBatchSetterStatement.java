package net.cap5lut.database;

import net.cap5lut.util.function.ConsumerEx;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    S add(ConsumerEx<PreparedStatement, SQLException> setter);

    /**
     * Adds a batch.
     *
     * @param batch batch
     * @return itself for method chaining
     */
    default S add(Batch batch) {
        return add(batch::set);
    }
}
