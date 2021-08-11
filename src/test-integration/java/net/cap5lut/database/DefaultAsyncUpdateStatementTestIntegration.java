package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAsyncUpdateStatementTestIntegration {
    TestIntegrationDatabase database;

    @BeforeEach
    void beforeEach() {
        database = TestIntegrationDatabase.newInstance();
    }

    @AfterEach
    void afterAll() {
        database.close();
        database = null;
    }

    @Test
    public void execute() {
        assertEquals(
                1,
                new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        database.getDataSource()::getConnection,
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .addParameter(5)
                        .execute()
                        .join()
        );
        assertEquals(
                5,
                database
                        .query("SELECT id FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void execute_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        TestIntegrationDatabase.newBrokenConnectionFactory(),
                        "INSERT INTO test_table (id) VALUES ?;"
                )
                        .execute()
                        .join()
        );
    }

    @Test
    public void execute_read() {
        assertEquals(
                5,
                new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        database.getDataSource()::getConnection,
                        "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
                )
                        .addParameter(5)
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }

    @Test
    public void execute_read_noResult() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        database.getDataSource()::getConnection,
                        "SELECT id FROM test_table WHERE id = ?;"
                )
                        .addParameter(6)
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }

    @Test
    void execute_read_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        TestIntegrationDatabase.newBrokenConnectionFactory(),
                        "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
                )
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }
}