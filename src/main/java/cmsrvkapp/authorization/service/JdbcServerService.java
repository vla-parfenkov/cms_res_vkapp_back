package cmsrvkapp.authorization.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class JdbcServerService implements ServerService {


    private JdbcTemplate template;

    public JdbcServerService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public String getUrl() {
        String sql = "SELECT server_url FROM servers ORDER BY instance_count LIMIT 1";
        List<Map<String, Object>> result = template.queryForList(sql);
        return (String) result.get(0).get("server_url");
    }

    @Override
    public void addServer(String url) {
        String sql = "INSERT INTO servers (server_url) VALUES (?)";
        template.update(sql, url);
    }

    @Override
    public void delServer(String url) {
        String sql = "DELETE FROM servers WHERE server_url = ?";
        template.update(sql, url);
    }

    @Override
    public void instanceWasRemoved(String url) {
        String sql = "UPDATE servers SET instance_count = instance_count - 1 WHERE server_url = ?";
        template.update(sql, url);
    }

    @Override
    public void instanceWasAdded(String url) {
        String sql = "UPDATE servers SET instance_count = instance_count + 1 WHERE server_url = ?";
        template.update(sql, url);
    }

    @Override
    public String getKey(String url) {
        String sql = "SELECT server_key FROM servers WHERE server_url = ?";
        List<Map<String, Object>> result = template.queryForList(sql, url);
        return (String) result.get(0).get("server_key");
    }


    @Override
    public Boolean checkKey(String key) {
        String sql = "SELECT * FROM servers WHERE server_key = ?";
        List<Map<String, Object>> result = template.queryForList(sql, key);
        return result.isEmpty();
    }
}
