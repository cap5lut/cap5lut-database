package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTransactionContextTest {
    @Test
    public void batch() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeBatch()).thenReturn(new int[] {1, 1, 1});

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertArrayEquals(
                new int[] {1, 1, 1},
                new DefaultTransactionContext(connection)
                        .batch("INSERT INTO test_table (id) VALUES (?);")
                        .add(new Batch().addParameter(1))
                        .add(new Batch().addParameter(2))
                        .add(new Batch().addParameter(3))
                        .executeBatch()
        );
    }

    @Test
    public void create() throws SQLException {
        final var statement = mock(Statement.class);

        final var connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);

        new DefaultTransactionContext(connection)
                .create("CREATE TABLE test_table2 (id INT); DROP TABLE test_table2;");
    }

    @Test
    public void query() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                5,
                new DefaultTransactionContext(connection)
                        .query("SELECT 5;")
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    public void update() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                5,
                new DefaultTransactionContext(connection)
                        .update("INSERT INTO test_table (id) VALUES (5) RETURNING id")
                        .<Integer>execute(row -> row.getInt(1))
        );
    }

    @Test
    public void transaction_simple() throws SQLException {
        final var insertStatement = mock(PreparedStatement.class);

        final var queryResult = mock(ResultSet.class);
        when(queryResult.next()).thenReturn(true).thenReturn(false);
        when(queryResult.getInt(1)).thenReturn(0);

        final var queryStatement = mock(PreparedStatement.class);
        when(queryStatement.executeQuery()).thenReturn(queryResult);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(insertStatement).thenReturn(queryStatement);

        final var context = new DefaultTransactionContext(connection);
        context.update("INSERT INTO test_table (id) VALUES (1);").execute();
        context.rollback();
        context.commit();
        assertEquals(
                0,
                context
                        .query("SELECT count(*) FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    public void transaction_named() throws SQLException {
        final var insertStatement = mock(PreparedStatement.class);

        final var queryResult = mock(ResultSet.class);
        when(queryResult.next()).thenReturn(true).thenReturn(false);
        when(queryResult.getInt(1)).thenReturn(1);

        final var queryStatement = mock(PreparedStatement.class);
        when(queryStatement.executeQuery()).thenReturn(queryResult);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString()))
                .thenReturn(insertStatement)
                .thenReturn(insertStatement)
                .thenReturn(queryStatement);

        final var context = new DefaultTransactionContext(connection);
        context.update("INSERT INTO test_table (id) VALUES (1);").execute();
        final var savepoint = context.savepoint("between");
        context.update("INSERT INTO test_table (id) VALUES (2);").execute();
        context.rollback(savepoint);
        context.commit();
        assertEquals(
                1,
                context
                        .query("SELECT count(*) FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    public void transaction_unnamed() throws SQLException {
        final var insertStatement = mock(PreparedStatement.class);

        final var queryResult = mock(ResultSet.class);
        when(queryResult.next()).thenReturn(true).thenReturn(false);
        when(queryResult.getInt(1)).thenReturn(1);

        final var queryStatement = mock(PreparedStatement.class);
        when(queryStatement.executeQuery()).thenReturn(queryResult);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString()))
                .thenReturn(insertStatement)
                .thenReturn(insertStatement)
                .thenReturn(queryStatement);

        final var context = new DefaultTransactionContext(connection);
        context.update("INSERT INTO test_table (id) VALUES (1);").execute();
        final var savepoint = context.savepoint();
        context.update("INSERT INTO test_table (id) VALUES (2);").execute();
        context.rollback(savepoint);
        context.commit();
        assertEquals(
                1,
                context
                        .query("SELECT count(*) FROM test_table;")
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }
}