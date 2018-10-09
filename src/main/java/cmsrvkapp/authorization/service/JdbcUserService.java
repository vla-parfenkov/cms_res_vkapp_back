package cmsrvkapp.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import cmsrvkapp.authorization.views.UserView;


@Service
public class JdbcUserService implements UserService {

    private JdbcTemplate template;

    @Autowired
    private ObjectMapper objectMapper;

    private static final RowMapper<UserView> READ_USER_MAPPER = (resultSet, rowNumber) ->
            new UserView(resultSet.getString("email"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));


    public JdbcUserService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void addUser(UserView user) {
        String sql = "INSERT INTO users (email, login, password) VALUES (?, ?, ?)";
        template.update(sql, user.getEmail(), user.getLogin(), user.getPassword());
    }

    @Override
    public UserView getByLoginOrEmail(String loginOrEmail) {
        String sql = "SELECT DISTINCT email, login, password FROM users WHERE email = ? OR login = ?";
        return template.queryForObject(sql, READ_USER_MAPPER, loginOrEmail, loginOrEmail);
    }

    @Override
    public UserView changeUser(UserView user) {
        String sql = "UPDATE users SET (email, password) = (?, ?) WHERE login = ?";
        if (template.update(sql, user.getEmail(), user.getPassword(), user.getLogin()) != 0) {
            return user;
        }
        return null;
    }
}
