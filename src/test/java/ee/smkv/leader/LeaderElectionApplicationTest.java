package ee.smkv.leader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

class LeaderElectionApplicationTest {

    public static final String DB = "jdbc:h2:file:./testdb;AUTO_SERVER=TRUE";
    EmbeddedDbHelper dbHelper = new EmbeddedDbHelper(DB);

    @BeforeEach
    void setUp() {
        dbHelper.normalInitialization();
    }

    @AfterEach
    void tearDown() {
        dbHelper.destroy();
    }

    @Test
    void singleApplication() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        try {
            Application application = context.getBean(Application.class);
            Assertions.assertTrue(application.leader);
        } finally {
            context.close();
        }
    }

    @Test
    void multipleApplications() {
        AnnotationConfigApplicationContext app1 = new AnnotationConfigApplicationContext(Application.class);
        AnnotationConfigApplicationContext app2 = new AnnotationConfigApplicationContext(Application.class);
        AnnotationConfigApplicationContext app3 = new AnnotationConfigApplicationContext(Application.class);

        try {
            Assertions.assertTrue(app1.getBean(Application.class).leader);
            Assertions.assertFalse(app2.getBean(Application.class).leader);
            Assertions.assertFalse(app3.getBean(Application.class).leader);
        } finally {
            app1.close();
            app2.close();
            app3.close();
        }
    }

    @Test
    void multipleApplications_changeLeader() {
        AnnotationConfigApplicationContext app1 = new AnnotationConfigApplicationContext(Application.class);
        AnnotationConfigApplicationContext app2 = new AnnotationConfigApplicationContext(Application.class);
        AnnotationConfigApplicationContext app3 = new AnnotationConfigApplicationContext(Application.class);

        try {
            app1.close();
            app2.getBean(LeaderElectionWorker.class).checkLeader();
            app3.getBean(LeaderElectionWorker.class).checkLeader();
            boolean secondLeader = app2.getBean(Application.class).leader;
            boolean thirdLeader = app3.getBean(Application.class).leader;
            Assertions.assertTrue(secondLeader);
            Assertions.assertFalse(thirdLeader);
        } finally {
            app1.close();
            app2.close();
            app3.close();
        }
    }

    @EnableLeaderElectionJdbc
    @Component
    @Configuration
    static class Application implements LeaderElectionListener {


        @Bean
        DataSource dataSource() {
            return new EmbeddedDbHelper(DB).getDataSource();
        }

        boolean leader;

        @Override
        public void granted() {
            leader = true;
        }

        @Override
        public void revoked() {
            leader = false;
        }
    }
}
