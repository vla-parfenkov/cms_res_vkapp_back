package cmsrvkapp.authorization.service;

import cmsrvkapp.authorization.views.ApplicationView;
import cmsrvkapp.authorization.views.ResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;


public class JdbcApplicationService implements ApplicationService {

    private JdbcTemplate template;

    @Autowired
    private ObjectMapper objectMapper;

    private static final RowMapper<String> READ_CONFIG_MAPPER =
            (resultSet, rowNumber) -> resultSet.getString("config");

    private static final RowMapper<ApplicationView> READ_APPLICATION_MAPPER = (resultSet, rowNumber) ->
            new ApplicationView(resultSet.getString("app_name"),
                    resultSet.getString("creator_login"));

    public JdbcApplicationService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void addApplication(ApplicationView app) {
        String sql = "INSERT INTO applications (app_name, creator_login) VALUES (?, ?)";
        template.update(sql, app.getAppName(), app.getCreatorLogin());
    }

    @Override
    public ApplicationView getByName(String appName) {
        String sql = "SELECT DISTINCT app_name, creator_login FROM applications WHERE app_name = ?";
        return template.queryForObject(sql, READ_APPLICATION_MAPPER, appName);
    }

    @Override
    public ApplicationView changeApplication(ApplicationView app) {
        String sql = "UPDATE application SET (app_name, creator_login) = (?, ?) WHERE app_name = ?";
        if (template.update(sql, app.getAppName(), app.getCreatorLogin(), app.getAppName()) != 0) {
            return app;
        }
        return null;
    }


    @Override
    public String getConfig(ApplicationView app) {
        String sql = "SELECT config FROM applications WHERE creatorLogin = ?";
        String config = template.queryForObject(sql, READ_CONFIG_MAPPER, app.getCreatorLogin());
        if (config != null) {
            return config;
        } else {
            throw new IllegalArgumentException(ResponseView.ERROR_CONFIG_NOT_FOUND.getResponse());
        }
    }

    @Override
    public void setConfig(ApplicationView app, String config) {
        PGobject pgobject = new PGobject();
        pgobject.setType("jsonb");
        try {
            pgobject.setValue(config);
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ResponseView.ERROR_BAD_CONFIG.getResponse());
        }
        String sql = "UPDATE applications SET config = ? WHERE creatorLogin = ?";
        template.update(sql, pgobject, app.getCreatorLogin());
    }
}
