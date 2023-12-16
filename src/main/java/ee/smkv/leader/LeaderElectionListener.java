package ee.smkv.leader;

public interface LeaderElectionListener {
    void granted();
    void revoked();
}
