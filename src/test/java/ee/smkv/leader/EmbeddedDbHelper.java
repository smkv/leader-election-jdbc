package ee.smkv.leader;

import org.h2.tools.DeleteDbFiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Timestamp;

public class EmbeddedDbHelper {
    private final String url;
    private SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public EmbeddedDbHelper(String url) {
        this.url = url;
        this.dataSource = new SingleConnectionDataSource(this.url, false);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void normalInitialization() {
        createTable();
        insertEmptyRecord();
    }

    public void createTable() {
        jdbcTemplate.execute("CREATE TABLE LEADER (NAME VARCHAR(36), UNTIL TIMESTAMP)");
    }

    public void insertEmptyRecord() {
        jdbcTemplate.update("INSERT INTO LEADER (NAME, UNTIL) VALUES (NULL, NULL)");
    }


    public void updateRecord(String name, Timestamp until) {
        jdbcTemplate.update("UPDATE LEADER SET NAME = ?, UNTIL = ?", name, until);
    }

    public void destroy() {
        dataSource.destroy();
        if (url.startsWith("jdbc:h2:file:")) {
            String location = url.substring(13).split(";")[0].replaceAll("\\\\", "/");
            String dir = location.substring(0, location.indexOf('/'));
            String file = location.substring(location.indexOf('/') + 1);
            DeleteDbFiles.execute(dir, file, true);
        }
    }
}
