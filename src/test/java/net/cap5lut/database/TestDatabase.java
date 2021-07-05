package net.cap5lut.database;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public final class TestDatabase extends DefaultDatabase implements AutoCloseable {
    public static TestDatabase newInstance() {
        try {
            final var database = new TestDatabase();
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

    protected TestDatabase(EmbeddedPostgres pg) {
        super(pg.getPostgresDatabase(), ForkJoinPool.commonPool());
        this.pg = pg;
    }

    protected TestDatabase() throws IOException {
        this(EmbeddedPostgres.start());
    }

    public DataSource getDataSource() {
        return pg.getPostgresDatabase();
    }

    @Override
    public void close() {
        try {
            pg.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
