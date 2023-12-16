package ee.smkv.leader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
@Configuration
public class LeaderElectionConfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    public LeaderElectionProperties leaderTableProperties() {
        return new LeaderElectionProperties();
    }

    @Bean
    public LeaderElectionService leaderElectionService(LeaderElectionProperties properties) {
        DataSource dataSource;
        String dataSourceName = properties.getDataSourceName();
        if (dataSourceName == null || dataSourceName.isEmpty()) {
            dataSource = context.getBean(DataSource.class);
        } else {
            dataSource = context.getBean(dataSourceName, DataSource.class);
        }
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
