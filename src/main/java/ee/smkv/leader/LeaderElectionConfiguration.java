package ee.smkv.leader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LeaderElectionConfiguration {

    @Bean
    public LeaderElectionProperties leaderTableProperties() {
        return new LeaderElectionProperties();
    }

    @Bean
    public LeaderElectionService leaderElectionService(DataSource dataSource, LeaderElectionProperties properties) {
        return new JdbcLeaderElectionServiceImpl(dataSource, properties);
    }

    @Bean
    public LeaderElectionWorker leaderElectionWorker(LeaderElectionService leaderElectionService, LeaderElectionProperties properties, LeaderElectionBeanPostProcessor leaderElectionBeanPostProcessor) {
        return new LeaderElectionWorker(leaderElectionService, properties, leaderElectionBeanPostProcessor.getListeners());
    }

    @Bean
    public LeaderElectionBeanPostProcessor leaderElectionBeanPostProcessor() {
        return new LeaderElectionBeanPostProcessor();
    }
}
