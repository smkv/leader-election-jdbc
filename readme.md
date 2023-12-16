# Leader Election JDBC
A small library for Spring application for multi microservice architecture to choose leader instance based on JDBC connection and small one-record table.

## How-to
### Create a small table
Example of table:
```sql
CREATE TABLE LEADER (NAME VARCHAR(36), UNTIL TIMESTAMP);
INSERT INTO LEADER (NAME, UNTIL) VALUES (NULL, NULL);
```
### Add library to your project
**NB! Library not published yet.**
Gradle example:
```gradle
 implementation 'ee.smkv:leader-election-jdbc:0.1.0'
```

### Add application properties
All properties optional, there are default values:
```properties
leader-election-jdbc.table-name=LEADER
leader-election-jdbc.field-name=NAME
leader-election-jdbc.field-until=UNTIL
# define instance name or leave empty - it will generate random UUID
leader-election-jdbc.name=
# Leadership checking interval, by default every 5 seconds
leader-election-jdbc.pool-interval=5000
# Leadership locking interval, nobody can take leadership during this period, by default every 6 seconds
leader-election-jdbc.lock-interval=6000
```

### Add annotation to your Spring application
```java
@EnableLeaderElectionJdbc
public class MyApplication {
```