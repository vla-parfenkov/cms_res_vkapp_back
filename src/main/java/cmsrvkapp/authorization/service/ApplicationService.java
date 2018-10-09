package cmsrvkapp.authorization.service;

import cmsrvkapp.authorization.views.ApplicationView;

public interface ApplicationService {

    void addApplication(ApplicationView app);

    ApplicationView getByName(String appName);

    ApplicationView changeApplication(ApplicationView user);

    String getConfig(ApplicationView app);

    void setConfig(ApplicationView app, String config);
}
