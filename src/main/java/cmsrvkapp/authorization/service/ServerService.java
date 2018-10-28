package cmsrvkapp.authorization.service;

public interface ServerService {

    String getUrl();

    void addServer(String url);

    void delServer(String url);

    void instanceWasRemoved(String url);

    void instanceWasAdded(String url);

    String getKey(String url);

    Boolean checkKey(String key);
}
