package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DefaultAsyncBatchStatementTest {
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
    public void execute() {
        final var result = new DefaultAsyncBatchStatement(
                ForkJoinPool.commonPool(),
                database.getDataSource()::getConnection,
                "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .execute(row -> row.getInt(1))
                .join()
                .mapToInt(i -> i)
                .toArray();
        assertArrayEquals(new int[] {1, 2, 3}, result);
    }

    @Test
    void execute_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncBatchStatement(
                        ForkJoinPool.commonPool(),
                        TestDatabase.newBrokenConnectionFactory(),
                        "SELECT ?;"
                )
                        .add(new Batch().addParameter(1))
                        .add(new Batch().addParameter(2))
                        .add(new Batch().addParameter(3))
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }

    @Test
    public void executeBatch() {
        final var result = new DefaultAsyncBatchStatement(
                ForkJoinPool.commonPool(),
                database.getDataSource()::getConnection,
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeBatch()
                .join();
        assertArrayEquals(new int[] {1, 1, 1}, result);
    }

    @Test
    void executeBatch_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncBatchStatement(
                        ForkJoinPool.commonPool(),
                        TestDatabase.newBrokenConnectionFactory(),
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .add(new Batch().addParameter(1))
                        .add(new Batch().addParameter(2))
                        .add(new Batch().addParameter(3))
                        .executeBatch()
                        .join()
        );
    }

    @Test
    public void executeLargeBatch() {
        final var result = new DefaultAsyncBatchStatement(
                ForkJoinPool.commonPool(),
                database.getDataSource()::getConnection,
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeLargeBatch()
                .join();
        assertArrayEquals(new long[] {1, 1, 1}, result);
    }

    @Test
    void executeLargeBatch_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncBatchStatement(
                        ForkJoinPool.commonPool(),
                        TestDatabase.newBrokenConnectionFactory(),
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .add(new Batch().addParameter(1))
                        .add(new Batch().addParameter(2))
                        .add(new Batch().addParameter(3))
                        .executeLargeBatch()
                        .join()
        );
    }
}