package ee.smkv.leader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LeaderElectionConfiguration {
    @Bean
    public LeaderElectionService leaderElectionService(DataSource dataSource) {
        return new JdbcLeaderElectionServiceImpl(dataSource);
    }

    @Bean
    public LeaderElectionWorker leaderElectionWorker(LeaderElectionService leaderElectionService, LeaderElectionBeanPostProcessor leaderElectionBeanPostProcessor) {
        return new LeaderElectionWorker(leaderElectionService, leaderElectionBeanPostProcessor.getListeners());
    }

    @Bean
    public LeaderElectionBeanPostProcessor leaderElectionBeanPostProcessor() {
        return new LeaderElectionBeanPostProcessor();
    }
}
