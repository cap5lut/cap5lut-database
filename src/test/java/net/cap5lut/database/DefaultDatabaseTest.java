package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultDatabaseTest {
    @Test
    void create() throws SQLException {
        final var statement = mock(Statement.class);

        final var connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);

        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                .create("DROP TABLE test_table;")
                .join();
    }

    @Test
    void create_throws_exception() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> {
                    final var statement = mock(Statement.class);
                    when(statement.execute(anyString())).thenThrow(SQLException.class);

                    final var connection = mock(Connection.class);
                    when(connection.createStatement()).thenReturn(statement);

                    final var dataSource = mock(DataSource.class);
                    when(dataSource.getConnection()).thenReturn(connection);

                    new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                            .create("DROP TABLE not_existing_table;")
                            .join();
                });
    }

   @Test
    void query() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("test");

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        assertEquals(
                "test",
                new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                        .query("SELECT 'test';")
                        .execute(row -> row.getString(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void update() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        assertEquals(
                1,
                new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                        .update("INSERT INTO test_table (id) VALUES (?);")
                        .addParameter(5)
                        .execute()
                        .join()
        );
    }

    @Test
    void batch() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeBatch()).thenReturn(new int[] {1, 1, 1});

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        final var results = new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                .batch("INSERT INTO test_table (id) VALUES (?);")
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeBatch()
                .join();
        assertArrayEquals(new int[] {1, 1, 1}, results);
    }

    @Test
    void transaction() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        final var expectedResult = new Object();
        final var actualResult = new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                .transaction(ctx -> {
                    assertEquals(
                            1,
                            ctx.update("INSERT INTO test_table (value) VALUES (?);").addParameter(1).execute()
                    );
                    return expectedResult;
                })
                .join();

        assertSame(expectedResult, actualResult);
    }

    @Test
    public void transaction_rollbackOnError() throws SQLException {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> {
                    final var statement = mock(PreparedStatement.class);
                    when(statement.executeUpdate()).thenThrow(SQLException.class);

                    final var connection = mock(Connection.class);
                    when(connection.prepareStatement(anyString())).thenReturn(statement);

                    final var dataSource = mock(DataSource.class);
                    when(dataSource.getConnection()).thenReturn(connection);

                    new DefaultDatabase(dataSource, ForkJoinPool.commonPool(), null)
                            .transaction(ctx -> {
                                ctx.update("INSERT INTO test_table (id) VALUES 6;").execute();
                                return null;
                            })
                            .join();
                }
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

    @Test
    public void close() {
        new DefaultDatabase(null, null, null).close();

        final var closeWasExecuted = new AtomicBoolean(false);
        new DefaultDatabase(null, null, () -> closeWasExecuted.set(true)).close();
        assertTrue(closeWasExecuted.get());
    }
}