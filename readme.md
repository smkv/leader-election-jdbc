# Leader Election JDBC

A small library for Spring application for multi microservice architecture to choose leader instance based on JDBC
connection and small one-record table.

## How-to

### Create a small table

Example of table:

```sql
CREATE TABLE LEADER
(
  NAME  VARCHAR(36),
  UNTIL TIMESTAMP
);
INSERT INTO LEADER (NAME, UNTIL)
VALUES (NULL, NULL);
```

### Add annotation to your Spring application

```java

@EnableLeaderElectionJdbc
public class MyApplication {
}
```

### Configuration

All properties are optional:

* `leader-election-jdbc.datasource` - You can define name of `java.sql.DataSource` bean in case of project with multiple
  database connections.
* `leader-election-jdbc.table-name` - Default: `LEADER`. You can redefine table name as you wish, one of example to add
  schema name to table.
* `leader-election-jdbc.field-name` - Default: `NAME`. You can redefine table field there the application keep leader
  name.
* `leader-election-jdbc.field-until` - Default: `UNTIL`. You can redefine table field there the application keep locking
  time.
* `leader-election-jdbc.name` - Default: generates random `UUID`. You can explicit instance name here.
* `leader-election-jdbc.pool-interval` - Default: `5000`. Pooling interval in milliseconds. This parameter affect how
  often leadership will be checked. If the value will be too small it will create higher load on system, but you can
  switch leader with smaller time gap.
* `leader-election-jdbc.lock-interval` - Default: `6000`. Locking interval in milliseconds. This parameter affect how
  fast leadership can be changed in case of the leader instance will crash without releasing lock. The value cannot be
  smaller than pooling interval.   
