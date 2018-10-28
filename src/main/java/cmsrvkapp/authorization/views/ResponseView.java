package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseView {
    ERROR_NOT_LOGGED_IN(0, "You are not currently logged in!"),
    ERROR_BAD_LOGIN_DATA(1, "Wrong login/email or password!"),
    SUCCESS_LOGOUT(3, "You successfully logged out!"),
    ERROR_USER_ALREADY_EXISTS(4, "User already exists!"),
    ERROR_USER_NOT_FOUND(5, "User not found!"),
    ERROR_NO_RIGHTS_TO_CHANGE_USER(6, "You have no rights to change this user data!"),
    ERROR_NO_RIGHTS_CONFIG(7, "You have no rights to look this user configuration!"),
    SUCCESS_SET_CONFIG(8, "The configuration was successfully downloaded!"),
    ERROR_CONFIG_NOT_FOUND(9, "Configuration not found!"),
    ERROR_BAD_CONFIG(10, "Bad Configuration!"),
    ERROR_APP_ALREADY_EXISTS(11, "Applications already exists!"),
    ERROR_APP_NOT_FOUND(12, "Application not found!"),
    ERROR_NO_RIGHTS_TO_CHANGE_APP(13, "You have no rights to change this app data!"),
    ERROR_DEPLOY(14, "Error deploy, try again"),
    ALREADY_DEPLOYED(15, "Already deployed");


    private final Integer status;
    private final String response;

    ResponseView(Integer status, String response) {
        this.status = status;
        this.response = response;
    }


    @SuppressWarnings("unused")
    public Integer getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public String getResponse() {
        return response;
    }

}
