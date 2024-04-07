package HarmoAIze.server.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class S3Repository {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public S3Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void saveFileURL(String fileName, String fileURL) {
        String insertSql = "insert into url(filename, url) values (?, ?);";
        jdbcTemplate.update(insertSql, fileName, fileURL);
    }
}
