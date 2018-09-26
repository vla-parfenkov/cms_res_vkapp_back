package cmsrvkapp.authorization.service;

import cmsrvkapp.config.ClientConfig;
import cmsrvkapp.authorization.views.ResponseView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import cmsrvkapp.authorization.views.UserView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


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

    private static final RowMapper<ClientConfig> READ_CONFIG_MAPPER = (resultSet, rowNumber) -> {
        try {
            return new ObjectMapper().readValue(resultSet.getString("config"), ClientConfig.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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
    public ClientConfig getConfig(UserView user) {
        String sql = "SELECT config FROM users WHERE login = ?";
        ClientConfig config = template.queryForObject(sql, READ_CONFIG_MAPPER, user.getLogin());
        if(config != null) {
            return config;
        } else {
            throw new IllegalArgumentException(ResponseView.ERROR_CONFIG_NOT_FOUND.getResponse());
        }
    }

    @Override
    public void setConfig(UserView user, ClientConfig config) {
        String sql = "UPDATE users SET config = ? WHERE login = ?";
        PGobject pGobject = new PGobject();
        pGobject.setType("jsonb");
        String json = null;
        try {
            json = objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ResponseView.ERROR_BAD_CONFIG.getResponse());
        }
        try {
            pGobject.setValue(json);
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ResponseView.ERROR_BAD_CONFIG.getResponse());
        }
        template.update(sql, pGobject, user.getLogin());
    }
}
