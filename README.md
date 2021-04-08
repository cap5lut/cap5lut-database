# cap5lut-database

***This library is still WIP!***

This library is a small wrapper for JDBC to avoid boilerplate. Additionally, all interactions with the database are asynchronous.

## Prerequisites
- Java 11
- a **pooled** DataSource

## Examples

### Creating a database instance
To create a Database instance, you simply need a DataSource. Here is an example using PostgreSQL and HikariCP:
````java
final var postgresDataSource = new PGSimpleDataSource();
postgresDataSource.setUrl(jdbcUrl);

final var hikariConfig = new HikariConfig();
hikariConfig.setDataSource(dataSource);

final var database = Database.of(new HikariDataSource(hikariConfig), Executors.newFixedThreadPool(5));
````

### Create statements
Create statements are statements, which will be just executed, do not take any/provide
any data.

```java
database
    .create("CREATE TABLE example_table (id INT PRIMARY KEY);")
    .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
    });
```

### Query statements
Query statements are statements to fetch data from the database.

```java
database
    .query("SELECT id FROM example_table WHERE id = ? LIMIT 1;")
    .addParameter(15)
    .execute(rs -> rs.getInt(1))
    .thenApply(Stream::findAny)
    .thenApply(Optional::orElseThrow)
    .thenRun(id -> System.out.printf("ID %d found.", id))
    .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
    });
```

### Update statements
Update statements are statements to modify data in the database.

```java
database
    .update("DELETE FROM example_table WHERE id % 2 == ?;")
    .addParameter(1)
    .execute()
    .thenRun(num -> System.out.printf("%d rows have been deleted.%n", num))
    .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
    });
```

### Batch statements
Batch statements are statements to for example insert multiple rows using the same statement.

```java
database
    .update("INSERT INTO example_table (id) VALUES (?) ON CONFLICT(id) DO NOTHING.")
        .add(statement ->  statement.setInt(1, 1))
        .add(statement ->  statement.setInt(1, 2))
        .add(new Batch().add(3))
        .add(new Batch().add(4))
    .executeBatch()
    .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
    });
```

### Transactions
Transactions are actions to execute multiple statements and roll back if any of them failed.

```java
database
    .transaction(context -> {
        context
            .update("INSERT INTO example_table (id) VALUES (?) ON CONFLICT(id) DO NOTHING;")
            .addParameter(5)
            .execute();
        
        context
            .update("INSERT INTO example_table2 (id, other_id) VALUES (?, ?)")
            .addParameter(17)
            .addParameter(5)
            .execute();
        
        context.commit();
    })
    .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
    });
```

## ToDo
- transactions should, if they failed, roll back automatically 
- add more data types to Batch
- unit and integration tests
- simple connection pool
- publishing