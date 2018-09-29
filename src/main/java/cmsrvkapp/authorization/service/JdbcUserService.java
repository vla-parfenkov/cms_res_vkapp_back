package cmsrvkapp.authorization.service;

import cmsrvkapp.config.ClientConfig;
import cmsrvkapp.authorization.views.ResponseView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import cmsrvkapp.authorization.views.UserView;

import java.io.IOException;
import java.sql.SQLException;


@SuppressWarnings("SqlNoDataSourceInspection")
@Service
public class JdbcUserService implements UserService {

    private JdbcTemplate template;

    @Autowired
    private ObjectMapper objectMapper;

    private static final RowMapper<UserView> READ_USER_MAPPER = (resultSet, rowNumber) ->
            new UserView(resultSet.getString("email"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));

    private static final RowMapper<String> READ_CONFIG_MAPPER = (resultSet, rowNumber) -> {
            return resultSet.getString("config");

    };



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


    @Override
    public String getConfig(UserView user) {
        String sql = "SELECT config FROM users WHERE login = ?";
        String config = template.queryForObject(sql, READ_CONFIG_MAPPER, user.getLogin());
        if (config != null) {
            return config;
        } else {
            throw new IllegalArgumentException(ResponseView.ERROR_CONFIG_NOT_FOUND.getResponse());
        }
    }

    @Override
    public void setConfig(UserView user, String config) {
        PGobject pgobject = new PGobject();
        pgobject.setType("jsonb");
        try {
            pgobject.setValue(config);
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ResponseView.ERROR_BAD_CONFIG.getResponse());
        }
        String sql = "UPDATE users SET config = ? WHERE login = ?";
        template.update(sql, pgobject, user.getLogin());
    }
}
