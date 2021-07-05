package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultTransactionContextTest {
    TestDatabase database;
    DefaultTransactionContext context;

    @BeforeEach
    void beforeEach() throws SQLException {
        database = TestDatabase.newInstance();
        final var connection = database.getDataSource().getConnection();
        connection.setAutoCommit(false);
        context = new DefaultTransactionContext(connection);
    }

    @AfterEach
    void afterEach() {
        context = null;
        database.close();
        database = null;
    }

    @Test
    public void batch() throws SQLException {
        assertArrayEquals(
                new int[] {1, 1, 1},
                context
                        .batch("INSERT INTO test_table (id) VALUES (?);")
                        .add(new Batch().addParameter(1))
                        .add(new Batch().addParameter(2))
                        .add(new Batch().addParameter(3))
                        .executeBatch()
        );
    }

    @Test
    public void create() throws SQLException {
        context.create("CREATE TABLE test_table2 (id INT); DROP TABLE test_table2;");
    }

    @Test
    public void query() throws SQLException {
        assertEquals(
                5,
                context.query("SELECT 5;").execute(row -> row.getInt(1)).findFirst().orElseThrow()
        );
    }

    @Test
    public void update() throws SQLException {
        assertEquals(
                5,
                context
                        .update("INSERT INTO test_table (id) VALUES (5) RETURNING id")
                        .<Integer>execute(row -> row.getInt(1))
        );
    }

    @Test
    public void transaction_simple() throws SQLException {
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