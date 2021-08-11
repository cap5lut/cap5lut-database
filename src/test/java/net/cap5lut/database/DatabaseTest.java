package net.cap5lut.database;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DatabaseTest {
    @Test
    void of_hikaricp() {
        assertTrue(Database.of(new HikariDataSource()) instanceof DefaultDatabase);
    }

    @Test
    void of_mocked() {
        assertTrue(Database.of(mock(DataSource.class)) instanceof DefaultDatabase);
    }
}