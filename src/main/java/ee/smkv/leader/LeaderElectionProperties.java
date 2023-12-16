package ee.smkv.leader;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public class LeaderElectionProperties {
    @Value("${leader-election-jdbc.table-name:LEADER}")
    private String leaderTableName = "LEADER";
    @Value("${leader-election-jdbc.field-name:NAME}")
    private String leaderNameFiled = "NAME";
    @Value("${leader-election-jdbc.field-until:UNTIL}")
    private String leaderUntilFiled = "UNTIL";
    @Value("${leader-election-jdbc.name:}")
    private String leaderName = "";
    @Value("${leader-election-jdbc.pool-interval:5000}")
    private long poolInterval = 5000;
    @Value("${leader-election-jdbc.lock-interval:6000}")
    private long lockInterval = 5000;


    public String getLeaderTableName() {
        return leaderTableName;
    }

    public String getLeaderNameFiled() {
        return leaderNameFiled;
    }

    public String getLeaderUntilFiled() {
        return leaderUntilFiled;
    }

    public String getLeaderName() {
        if (leaderName == null || leaderName.isEmpty()) {
            leaderName = UUID.randomUUID().toString();
        }
        return leaderName;
    }

    public long getPoolInterval() {
        return poolInterval;
    }

    public long getLockInterval() {
        return lockInterval;
    }
}
