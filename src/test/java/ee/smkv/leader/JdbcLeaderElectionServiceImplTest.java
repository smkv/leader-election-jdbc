package ee.smkv.leader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class JdbcLeaderElectionServiceImplTest {

    EmbeddedDbHelper dbHelper = new EmbeddedDbHelper("jdbc:h2:mem:testdb");
    JdbcLeaderElectionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new JdbcLeaderElectionServiceImpl(dbHelper.getDataSource(), new LeaderElectionProperties());
    }

    @AfterEach
    void tearDown() {
        dbHelper.destroy();
    }

    @Test
    void afterPropertiesSet_NoTable() {
        assertThrows(IllegalStateException.class, service::afterPropertiesSet);
    }

    @Test
    void afterPropertiesSet_TableEmpty() {
        dbHelper.createTable();
        assertThrows(IllegalStateException.class, service::afterPropertiesSet);
    }

    @Test
    void afterPropertiesSet_Ok() {
        dbHelper.normalInitialization();
        service.afterPropertiesSet();
    }

    @Test
    void afterPropertiesSet_TooManyRows() {
        dbHelper.normalInitialization();
        dbHelper.insertEmptyRecord();
        assertThrows(IllegalStateException.class, service::afterPropertiesSet);
    }

    @Test
    void isLeader_becameLeader() {
        dbHelper.normalInitialization();
        service.afterPropertiesSet();
        assertTrue(service.isLeader());
    }

    @Test
    void isLeader_becameLeader2() {
        dbHelper.normalInitialization();
        dbHelper.updateRecord("another", new Timestamp(System.currentTimeMillis() - 1));
        service.afterPropertiesSet();
        assertTrue(service.isLeader());
    }

    @Test
    void isLeader_anotherLeader() {
        dbHelper.normalInitialization();
        dbHelper.updateRecord("another", new Timestamp(System.currentTimeMillis() + 1000));
        service.afterPropertiesSet();
        assertFalse(service.isLeader());
    }

}