package net.cap5lut.database;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAsyncSelectStatementTestIntegration {
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
    void addParameter() throws IOException {
        try (final var pg = EmbeddedPostgres.start()) {
            assertEquals(
                    5,
                    new DefaultAsyncSelectStatement(
                            ForkJoinPool.commonPool(),
                            database.getDataSource()::getConnection,
                            "SELECT ?;"
                    )
                            .addParameter(5)
                            .execute(row -> row.getInt(1))
                            .join()
                            .findFirst()
                            .orElseThrow()
            );
        }
    }

    @Test
    void execute() {
        assertEquals(
                1,
                new DefaultAsyncSelectStatement(
                        ForkJoinPool.commonPool(),
                        database.getDataSource()::getConnection,
                        "SELECT 1;"
                )
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
                () -> new DefaultAsyncSelectStatement(
                        ForkJoinPool.commonPool(),
                        TestIntegrationDatabase.newBrokenConnectionFactory(),
                        "SELECT 1;"
                )
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }
}