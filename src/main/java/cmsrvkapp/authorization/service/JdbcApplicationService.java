package cmsrvkapp.authorization.service;

import cmsrvkapp.authorization.views.ApplicationState;
import cmsrvkapp.authorization.views.ApplicationView;
import cmsrvkapp.authorization.views.ResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class JdbcApplicationService implements ApplicationService {

    private JdbcTemplate template;

    @Autowired
    private ObjectMapper objectMapper;

    private static final RowMapper<String> READ_CONFIG_MAPPER =
            (resultSet, rowNumber) -> resultSet.getString("config");

    private static final RowMapper<ApplicationView> READ_APPLICATION_MAPPER = (resultSet, rowNumber) ->
            new ApplicationView(resultSet.getString("app_name"),
                    resultSet.getString("creator_login"), resultSet.getInt("service_id"),
                    resultSet.getString("server_url"),
                    ApplicationState.valueOf(resultSet.getString("app_state")));

    public JdbcApplicationService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void addApplication(ApplicationView app) {
        String sql = "INSERT INTO applications (app_name, creator_login, service_id) VALUES (?, ?, ?)";
        template.update(sql, app.getAppName(), app.getCreatorLogin(), app.getServiceId());
    }

    @Override
    public ApplicationView getByName(String appName) {
        String sql = "SELECT DISTINCT app_name, creator_login, service_id, server_url, app_state FROM applications WHERE app_name = ?";
        return template.queryForObject(sql, READ_APPLICATION_MAPPER, appName);
    }

    @Override
    public ApplicationView changeApplication(ApplicationView app) {
        String sql =
                "UPDATE application SET (app_name, creator_login, service_id, server_url, app_state) = (?, ?, ?, ?, ?) WHERE app_name = ?";
        if (template.update(sql, app.getAppName(), app.getCreatorLogin(), app.getServiceId(), app.getAppName()) != 0) {
            return app;
        }
        return null;
    }


    @Override
    public String getConfig(ApplicationView app) {
        String sql = "SELECT config FROM applications WHERE app_name = ?";
        String config = template.queryForObject(sql, READ_CONFIG_MAPPER, app.getAppName());
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
        String sql = "UPDATE applications SET config = ? WHERE app_name = ?";
        template.update(sql, pgobject, app.getAppName());
    }

    @Override
    public List<ApplicationView> getByCreatorLogin(String creatorLogin) {
        String sql = "SELECT app_name, creator_login, service_id, server_url, app_state FROM applications WHERE creator_login = ?";
        return template.query(sql, READ_APPLICATION_MAPPER, creatorLogin);
    }

    @Override
    public void setUrl(ApplicationView app) {
        String sql = "UPDATE applications SET server_url = ? WHERE app_name = ?";
        template.update(sql, app.getServerUrl(), app.getAppName());
    }

    @Override
    public void setState(ApplicationView app) {
        String sql = "UPDATE applications SET app_state = ? WHERE app_name = ?";
        template.update(sql, app.getState().name(), app.getAppName());
    }
}
