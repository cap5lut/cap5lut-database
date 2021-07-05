package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DefaultSyncBatchStatementTest {
    TestDatabase database;

    @BeforeEach
    void beforeEach() {
        database = TestDatabase.newInstance();
    }

    @AfterEach
    void afterAll() {
        database.close();
        database = null;
    }

    @Test
    public void execute() throws SQLException {
        final var result = new DefaultSyncBatchStatement(
                database.getDataSource().getConnection(),
                "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .execute(row -> row.getInt(1))
                .mapToInt(i -> i)
                .toArray();
        assertArrayEquals(new int[] {1, 2, 3}, result);
    }

    @Test
    public void executeBatch() throws SQLException {
        final var result = new DefaultSyncBatchStatement(
                database.getDataSource().getConnection(),
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeBatch();
        assertArrayEquals(new int[] {1, 1, 1}, result);
    }

    @Test
    public void executeLargeBatch() throws SQLException {
        final var result = new DefaultSyncBatchStatement(
                database.getDataSource().getConnection(),
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeLargeBatch();
        assertArrayEquals(new long[] {1, 1, 1}, result);
    }
}