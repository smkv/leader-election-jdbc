# Leader Election JDBC

A small library for Spring application for multi microservice architecture to choose leader instance based on JDBC
connection and small one-record table.

## How-to

### Create a table with only one record

Example of table:

```sql
CREATE TABLE LEADER
(
  NAME  VARCHAR(36),
  UNTIL TIMESTAMP -- or DATETIME or similar
);
INSERT INTO LEADER (NAME, UNTIL)
VALUES (NULL, NULL);
```

The database application user requires only update grants to this table.

### Add annotation to your Spring application

```java

@EnableLeaderElectionJdbc
public class MyApplication {
}
```

### Implement interface for a bean

Implement interface `ee.smkv.leader.LeaderElectionListener` for a bean you like know who is leader.

```java

@Service
public class MyService implements LeaderElectionListener {
  private boolean leader;

  @Override
  public void granted() {
    leader = true;
  }

  @Override
  public void revoked() {
    leader = false;
  }

  public void doWork() {
    if (leader) {
      // do something only if your instance is leader
    }
  }
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
* `leader-election-jdbc.name` - Default: generates random `UUID`. You can define explicit instance name here. For
  example, it can come outside the application - kubernetes.
* `leader-election-jdbc.pool-interval` - Default: `5000`. Pooling interval in milliseconds. This parameter affect how
  often leadership will be checked. If the value will be too small it will create higher load on system, but you can
  switch leader with smaller time gap.
* `leader-election-jdbc.lock-interval` - Default: `6000`. Locking interval in milliseconds. This parameter affect how
  fast leadership can be changed in case of the leader instance will crash without releasing lock. The value cannot be
  smaller than pooling interval.   
