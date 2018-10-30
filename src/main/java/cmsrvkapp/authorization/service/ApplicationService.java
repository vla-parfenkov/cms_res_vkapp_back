package cmsrvkapp.authorization.service;

import cmsrvkapp.authorization.views.ApplicationView;

import java.util.List;

public interface ApplicationService {

    void addApplication(ApplicationView app);

    ApplicationView getByName(String appName);

    List<ApplicationView> getByCreatorLogin(String creatorLogin);

    ApplicationView changeApplication(ApplicationView user);

    String getConfig(ApplicationView app);

    void setConfig(ApplicationView app, String config);

    void setUrl(ApplicationView app);

    void setState(ApplicationView app);
}
