package ee.smkv.leader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.function.Supplier;

public class JdbcLeaderElectionServiceImpl implements LeaderElectionService, InitializingBean {

    private final JdbcTemplate jdbcTemplate;
    private final LeaderElectionProperties properties;

    protected Supplier<Long> currentTimeMillis = System::currentTimeMillis;
    private String selectSql;
    private String updateSql;
    private String releaseSql;

    public JdbcLeaderElectionServiceImpl(DataSource dataSource, LeaderElectionProperties properties) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.properties = properties;
    }


    @Override
    public void afterPropertiesSet() {
        try {
            selectSql = String.format("SELECT %1$s, %2$s FROM %3$s", properties.getLeaderNameFiled(), properties.getLeaderUntilFiled(), properties.getLeaderTableName());
            updateSql = String.format("UPDATE %3$s SET %1$s = ?, %2$s = ? WHERE %1$s = ? OR %1$s IS NULL OR UNTIL < ? OR %2$s IS NULL", properties.getLeaderNameFiled(), properties.getLeaderUntilFiled(), properties.getLeaderTableName());
            releaseSql = String.format("UPDATE %3$s SET %1$s = NULL, %2$s = NULL", properties.getLeaderNameFiled(), properties.getLeaderUntilFiled(), properties.getLeaderTableName());
            getLeader();
        } catch (BadSqlGrammarException e) {
            throw new IllegalStateException("Most probably table does not exists:" + e.getMessage(), e);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("Table is empty, expected at only one record");
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("Table has more than 1 record, expected at only one record");
        }
    }

    private Leader getLeader() {
        return jdbcTemplate.queryForObject(selectSql, Leader::new);
    }

    @Override
    public boolean isLeader() {
        Leader leader = getLeader();
        long time = currentTimeMillis.get();
        String leaderName = properties.getLeaderName();
        if (Objects.equals(leader.name, leaderName)) {
            return tryBecameLeader(time, leaderName);
        }
        if (leader.until == null || time > leader.until.getTime()) {
            return tryBecameLeader(time, leaderName);
        }

        return false;
    }


    private boolean tryBecameLeader(long time, String leaderName) {
        Timestamp now = new Timestamp(time);
        Timestamp lockUntil = new Timestamp(time + properties.getLockInterval());

        int updated = jdbcTemplate.update(updateSql, leaderName, lockUntil, leaderName, now);
        return updated > 0;
    }


    @Override
    public void releaseLeader() {
        if (isLeader()) {
            jdbcTemplate.update(releaseSql);
        }
    }

    public class Leader {
        private final String name;
        private final Timestamp until;

        public Leader(ResultSet rs, int i) throws SQLException {
            this.name = rs.getString(properties.getLeaderNameFiled());
            this.until = rs.getTimestamp(properties.getLeaderUntilFiled());
        }
    }
}
