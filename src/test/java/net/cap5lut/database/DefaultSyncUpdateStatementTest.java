package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class DefaultSyncUpdateStatementTest {
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
        assertEquals(
                1,
                new DefaultSyncUpdateStatement(
                        database.getDataSource().getConnection(),
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .addParameter(5)
                        .execute()
        );
    }

    @Test
    public void execute_read() throws SQLException {
        assertEquals(
                5,
                new DefaultSyncUpdateStatement(
                        database.getDataSource().getConnection(),
                        "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
                )
                        .addParameter(5)
                        .<Integer>execute(row -> row.getInt(1))
        );
    }

    @Test
    public void execute_read_noResult() {
        assertThrows(
                SQLException.class,
                () -> {
                    final Connection connection;
                    try {
                        connection = database.getDataSource().getConnection();
                    } catch (SQLException e) {
                        fail(e);
                        return;
                    }
                    new DefaultSyncUpdateStatement(connection, "SELECT id FROM test_table WHERE id = ?;")
                            .addParameter(6)
                            .execute(row -> row.getInt(1));
                }
        );
    }
}