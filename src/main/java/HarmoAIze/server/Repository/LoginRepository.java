package HarmoAIze.server.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoginRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LoginRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String checkUser(String id, String pw) {
        String checkSql = "select nickname from user where id = ? and pw = ?;";
        List<String> nickname = jdbcTemplate.query(checkSql, (rs, rowNum) -> {
            return new String(
                    rs.getString("nickname"));
        }, id, pw);

        if(nickname.isEmpty())
            return "false";
        else
            return nickname.get(0);
    }
}
