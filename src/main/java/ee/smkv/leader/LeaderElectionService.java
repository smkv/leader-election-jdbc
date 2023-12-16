package ee.smkv.leader;

public interface LeaderElectionService {
    boolean isLeader();
    void releaseLeader();
}
