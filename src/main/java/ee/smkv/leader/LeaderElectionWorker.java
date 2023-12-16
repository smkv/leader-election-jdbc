package ee.smkv.leader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaderElectionWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderElectionWorker.class);
    private final LeaderElectionService leaderElectionService;
    private final Set<LeaderElectionListener> listeners;
    private boolean leader = false;
    private ScheduledExecutorService scheduledExecutorService;

    public LeaderElectionWorker(LeaderElectionService leaderElectionService, Set<LeaderElectionListener> listeners) {
        this.leaderElectionService = leaderElectionService;
        this.listeners = listeners;
    }

    @PostConstruct
    public void start() {
        LOGGER.info("Starting worker");
        checkLeader();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(this::checkLeader, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Stopping worker");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        if (leader) {
            leaderElectionService.releaseLeader();
        }
        notifyRevoked();
    }

    protected void checkLeader() {
        boolean newLeader = leaderElectionService.isLeader();
        boolean granted = newLeader != leader && newLeader;
        boolean revoked = newLeader != leader && !newLeader;
        this.leader = newLeader;
        if (granted) {
            notifyGranted();
        }
        if (revoked) {
            notifyRevoked();
        }
    }

    private void notifyRevoked() {
        LOGGER.info("Leader role revoked");
        listeners.forEach(LeaderElectionListener::revoked);
    }

    private void notifyGranted() {
        LOGGER.info("Leader role granted");
        listeners.forEach(LeaderElectionListener::granted);
    }
}
