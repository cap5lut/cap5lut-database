package net.cap5lut.database;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public final class TestIntegrationDatabase extends DefaultDatabase {
    public static TestIntegrationDatabase newInstance() {
        try {
            final var database = new TestIntegrationDatabase();
            database.create("CREATE TABLE test_table (id INT);").join();
            return database;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static SQLSupplier<Connection> newBrokenConnectionFactory() {
        return () -> {
            throw new SQLException("expected exception");
        };
    }

    private final EmbeddedPostgres pg;

    private TestIntegrationDatabase(EmbeddedPostgres pg) {
        super(pg.getPostgresDatabase(), ForkJoinPool.commonPool(), () -> {
            try {
                pg.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.pg = pg;
    }

    private TestIntegrationDatabase() throws IOException {
        this(EmbeddedPostgres.start());
    }

    public DataSource getDataSource() {
        return pg.getPostgresDatabase();
    }
}
