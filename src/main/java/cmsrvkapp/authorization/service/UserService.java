package cmsrvkapp.authorization.service;


import cmsrvkapp.config.ClientConfig;
import cmsrvkapp.authorization.views.UserView;


public interface UserService {

    void addUser(UserView user);

    UserView getByLoginOrEmail(String loginOrEmail);
    
    UserView changeUser(UserView user);

    String getConfig(UserView user);

    void setConfig(UserView user, String config);

}