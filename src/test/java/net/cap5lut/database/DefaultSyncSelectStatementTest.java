package net.cap5lut.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultSyncSelectStatementTest {
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
                5,
                new DefaultSyncSelectStatement(
                        database.getDataSource().getConnection(),
                        "SELECT ?;"
                )
                        .addParameter(5)
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }
}