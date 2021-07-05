package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test
    void of() {
        assertTrue(Database.of(null) instanceof DefaultDatabase);
    }
}