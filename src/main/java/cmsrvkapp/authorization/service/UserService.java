package cmsrvkapp.authorization.service;


import cmsrvkapp.authorization.views.UserView;


public interface UserService {

    void addUser(UserView user);

    UserView getByLoginOrEmail(String loginOrEmail);
    
    UserView changeUser(UserView user);

}