package ee.smkv.leader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 Andrei Samkov
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class LeaderElectionWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderElectionWorker.class);
    private final LeaderElectionService leaderElectionService;
    private final LeaderElectionProperties properties;
    private final Set<LeaderElectionListener> listeners;
    private boolean leader = false;
    private ScheduledExecutorService scheduledExecutorService;

    public LeaderElectionWorker(LeaderElectionService leaderElectionService, LeaderElectionProperties properties, Set<LeaderElectionListener> listeners) {
        this.leaderElectionService = leaderElectionService;
        this.properties = properties;
        this.listeners = listeners;
    }

    @PostConstruct
    public void start() {
        LOGGER.info("Starting worker {}", properties.getLeaderName());
        checkLeader();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(this::checkLeader, properties.getPoolInterval(), properties.getPoolInterval(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Stopping worker {}", properties.getLeaderName());
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
        LOGGER.info("Leader role revoked for {}", properties.getLeaderName());
        listeners.forEach(LeaderElectionListener::revoked);
    }

    private void notifyGranted() {
        LOGGER.info("Leader role granted for {}", properties.getLeaderName());
        listeners.forEach(LeaderElectionListener::granted);
    }
}
