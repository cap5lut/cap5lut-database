package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultDatabaseTestIntegration {
    DefaultDatabase database;

    @BeforeEach
    void beforeEach() {
        database = TestIntegrationDatabase.newInstance();
    }

    @AfterEach
    void afterAll() {
        ((TestIntegrationDatabase) database).close();
        database = null;
    }

    @Test
    void create() {
        database.create("DROP TABLE test_table;").join();
    }

    @Test
    void create_throws_exception() {
        assertThrowsWithCause(CompletionException.class, SQLException.class, () -> database.create("DROP TABLE not_existing_table;").join());
    }

   @Test
    void query() {
        assertEquals(
                "test",
                database.query("SELECT 'test';")
                        .execute(row -> row.getString(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void update() {
        assertEquals(
                1,
                database
                        .update("INSERT INTO test_table (id) VALUES (?);")
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
    void batch() {
        final var results = database
                .batch("INSERT INTO test_table (id) VALUES (?);")
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeBatch()
                .join();
        for (var result: results) {
            assertEquals(1, result);
        }
        assertEquals(
                3,
                database
                        .query("SELECT max(id) FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void transaction() {
        final var expectedResult = new Object();
        final var actualResult = database
                .transaction(ctx -> {
                    // fill
                    final var results = ctx.batch("INSERT INTO test_table (id) VALUES (?);")
                            .add(new Batch().addParameter(1))
                            .add(new Batch().addParameter(2))
                            .add(new Batch().addParameter(3))
                            .executeBatch();
                    for (var result : results) {
                        assertEquals(1, result);
                    }

                    // read
                    final var sum = ctx
                            .query("SELECT sum(id) FROM test_table;")
                            .execute(row -> row.getInt(1))
                            .findFirst()
                            .orElseThrow();

                    // insert new and remove old
                    ctx.update("INSERT INTO test_table (id) VALUES (?);")
                            .addParameter(sum)
                            .execute();
                    ctx.update("DELETE FROM test_table WHERE id < ?;")
                            .addParameter(sum)
                            .execute();

                    assertEquals(
                            6,
                            ctx
                                    .query("SELECT sum(id) FROM test_table;")
                                    .execute(row -> row.getInt(1))
                                    .findFirst()
                                    .orElseThrow()
                    );
                    assertEquals(
                            1,
                            ctx
                                    .query("SELECT count(id) FROM test_table;")
                                    .execute(row -> row.getInt(1))
                                    .findFirst()
                                    .orElseThrow()
                    );
                    return expectedResult;
                })
                .join();

        assertSame(
                expectedResult,
                actualResult
        );
    }

    @Test
    public void transaction_rollbackOnError() {
        database
                .update("INSERT INTO test_table (id) VALUES (5);")
                .execute()
                .join();

        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> database
                        .transaction(ctx -> {
                            ctx.update("INSERT INTO test_table (id) VALUES 6;");
                            throw new SQLException("test");
                        })
                        .join()
        );

        assertEquals(
                1,
                database
                        .query("SELECT count(*) FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    public void transaction_brokenConnection() throws SQLException {
        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("expected exception"));
        var database = new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null);

        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> database
                        .transaction(ctx -> {
                            ctx.commit();
                            return null;
                        })
                        .join()
        );
    }
}