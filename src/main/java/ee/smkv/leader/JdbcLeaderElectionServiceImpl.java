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
import java.util.UUID;
import java.util.function.Supplier;

public class JdbcLeaderElectionServiceImpl implements LeaderElectionService, InitializingBean {

    private final JdbcTemplate jdbcTemplate;
    private final String name = UUID.randomUUID().toString();
    protected Supplier<Long> currentTimeMillis = System::currentTimeMillis;

    public JdbcLeaderElectionServiceImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void afterPropertiesSet() {
        try {
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
        return jdbcTemplate.queryForObject("SELECT NAME, UNTIL FROM LEADER", Leader::new);
    }

    @Override
    public boolean isLeader() {
        Leader leader = getLeader();
        long time = currentTimeMillis.get();
        if (Objects.equals(leader.name, name)) {
            return tryBecameLeader(time);
        }
        if (leader.until == null || new Timestamp(time).after(leader.until)) {
            return tryBecameLeader(time);
        }

        return false;
    }


    private boolean tryBecameLeader(long time) {
        Timestamp now = new Timestamp(time);
        Timestamp lockUntil = new Timestamp(time + 5000);

        int updated = jdbcTemplate.update(
                "UPDATE LEADER SET NAME = ?, UNTIL = ? WHERE NAME = ? OR NAME IS NULL OR UNTIL < ? OR UNTIL IS NULL",
                name, lockUntil, name, now);
        return updated > 0;
    }


    @Override
    public void releaseLeader() {
        jdbcTemplate.update("UPDATE LEADER SET NAME = NULL, UNTIL = NULL");

    }

    public static class Leader {
        private final String name;
        private final Timestamp until;

        public Leader(ResultSet rs, int i) throws SQLException {
            this.name = rs.getString("NAME");
            this.until = rs.getTimestamp("UNTIL");
        }
    }
}
